package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonNumber;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;
import com.coloredcarrot.jsonapi.parsing.JsonException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ReflectJsonDeserializer
{
    
    private final UnsafeJsonDeserializerFactory unsafeFactory = new UnsafeJsonDeserializerFactory();
    
    private Method computeDeserializerMethod(Class<?> clazz)
    {
        Method method = scanForDeserializerMethod(clazz);
        if (method == null)
            return null;
        // Verify method signature
        if (!clazz.isAssignableFrom(method.getReturnType()))
            throw new IllegalArgumentException("JsonDeserializer " + method.getName() + " has bad return type " + method.getReturnType().getName() + "(expected subtype of " + clazz.getName() + ")");
        if (method.getParameterCount() != 1)
            throw new IllegalArgumentException("JsonDeserializer " + method.getName() + " has bad signature with " + method.getParameterCount() + " parameters (expected 1)");
        if (!JsonNode.class.isAssignableFrom(method.getParameterTypes()[0]))
            throw new IllegalArgumentException("JsonDeserializer " + method.getName() + " has bad signature with first parameter type " + method.getParameterTypes()[0].getName() + " (expected subtype of " + JsonNode.class.getName() + ")");
        return method;
    }
    
    private Method scanForDeserializerMethod(Class<?> clazz)
    {
        for (Method method : clazz.getDeclaredMethods())
            if (method.isAnnotationPresent(JsonDeserializer.class))
                return method;
        if (clazz.getSuperclass() != null)
            return scanForDeserializerMethod(clazz.getSuperclass());
        return null;
    }
    
    public <T> T deserialize(Class<T> clazz, JsonNode json)
    {
        try
        {
            return deserialize0(clazz, json);
        }
        catch (ReflectiveOperationException e)
        {
            throw new JsonException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T deserialize0(Class<T> clazz, JsonNode json) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        
        Method deserializerMethod = computeDeserializerMethod(clazz);
        if (deserializerMethod != null)
            return deserializeUsingDeserializer(deserializerMethod, json);
        
        if (json instanceof JsonNull)
            return null;
        
        if (clazz.isArray())
            return deserializeArray(clazz, json);
        
        if (Primitives.isPrimitiveOrWrapperClass(clazz) || clazz == String.class)
            return deserializePrimitive(clazz, json);
        
        if (Collection.class.isAssignableFrom(clazz))
            return deserializeCollection(clazz, json);
        if (Map.class.isAssignableFrom(clazz))
            return deserializeMap(clazz, json);
        if (clazz == Date.class)
            return (T) deserializeDate(json);
        if (clazz == UUID.class && !(json instanceof JsonObject))
            return (T) deserializeUUID(json);
    
        return deserializeFields(clazz, json);
        
    }
    
    protected <T> T deserializeFields(Class<T> clazz, JsonNode json) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        if (!(json instanceof JsonObject))
            throw new JsonException("Cannot deserialize fields for JSON nodes of type " + json.getClass().getSimpleName());
        return deserializeUnsafe(clazz, (JsonObject) json);
    }
    
    protected <T> T deserializeUnsafe(Class<T> clazz, JsonObject json) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        if (!unsafeFactory.isUnsafeAvailable())
            throw new JsonException(new ClassNotFoundException("sun.misc.Unsafe"));
        return unsafeFactory.getOrNull(clazz, this::deserialize).deserialize(json);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T deserializeArray(Class<T> clazz, JsonNode json) throws InstantiationException, IllegalAccessException
    {
        return (T) deserializeCollection(ArrayList.class, json).toArray();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> T deserializeCollection(Class<T> clazz, JsonNode json) throws IllegalAccessException, InstantiationException
    {
        Collection c = clazz == Collection.class || clazz == List.class ? new ArrayList() : (clazz == Set.class ? new HashSet() : (clazz == Queue.class ? new LinkedList() : (Collection) clazz.newInstance()));
        if (!(json instanceof JsonArray))
            c.add(freelyDeserialize(json));
        else
            for (JsonNode e : ((JsonArray) json).getContents())
                c.add(freelyDeserialize(e));
        return (T) c;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected  <T> T deserializeMap(Class<T> clazz, JsonNode json) throws IllegalAccessException, InstantiationException
    {
        Map c = clazz == Map.class ? new HashMap() : (clazz == ConcurrentMap.class ? new ConcurrentHashMap() : (Map) clazz.newInstance());
        if (!(json instanceof JsonObject))
            c.put("0", freelyDeserialize(json));
        else
            for (Map.Entry<String, JsonNode> e : ((JsonObject) json).getMappings().entrySet())
                c.put(e.getKey(), freelyDeserialize(e.getValue()));
        return (T) c;
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T deserializePrimitive(Class<T> clazz, JsonNode json)
    {
        if (clazz == String.class)
            return (T) json.stringValue();
        if (clazz == byte.class || clazz == Byte.class)
            return (T) Byte.valueOf(json instanceof JsonNumber ? ((JsonNumber) json).byteValue() : (byte) json.intValue());
        if (clazz == short.class || clazz == Short.class)
            return (T) Short.valueOf(json instanceof JsonNumber ? ((JsonNumber) json).shortValue() : (short) json.intValue());
        if (clazz == int.class || clazz == Integer.class)
            return (T) Integer.valueOf(json.intValue());
        if (clazz == long.class || clazz == Long.class)
            return (T) Long.valueOf(json instanceof JsonNumber ? ((JsonNumber) json).longValue() : (long) json.intValue());
        if (clazz == float.class || clazz == Float.class)
            return (T) Float.valueOf(json instanceof JsonNumber ? ((JsonNumber) json).floatValue() : (float) json.doubleValue());
        if (clazz == double.class || clazz == Double.class)
            return (T) Double.valueOf(json.doubleValue());
        if (clazz == boolean.class || clazz == Boolean.class)
            return (T) Boolean.valueOf(json.booleanValue());
        if (clazz == char.class || clazz == Character.class)
            return (T) Character.valueOf(json.stringValue().charAt(0));
        throw new JsonException();
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T deserializeUsingDeserializer(Method method, JsonNode json) throws InvocationTargetException, IllegalAccessException
    {
        method.setAccessible(true);
        return (T) method.invoke(null, json);
    }
    
    protected Date deserializeDate(JsonNode json)
    {
        return new Date(json.longValue());
    }
    
    protected UUID deserializeUUID(JsonNode json)
    {
        return UUID.fromString(json.stringValue());
    }
    
    public Object freelyDeserialize(JsonNode json)
    {
        if (json instanceof JsonString)
            return json.stringValue();
        if (json instanceof JsonInteger)
            return ((JsonInteger) json).longValue();
        if (json instanceof JsonDecimal)
            return json.doubleValue();
        if (json instanceof JsonBoolean)
            return json.booleanValue();
        if (json instanceof JsonNull)
            return null;
        if (json instanceof JsonArray)
            return ((JsonArray) json).getContents().stream().map(this::freelyDeserialize).collect(Collectors.toList());
        if (json instanceof JsonObject)
            return ((JsonObject) json).getMappings().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> freelyDeserialize(e.getValue())));
        throw new JsonException();
    }
    
}
