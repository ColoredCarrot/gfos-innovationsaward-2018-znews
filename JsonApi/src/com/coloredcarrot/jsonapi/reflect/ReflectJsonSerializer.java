package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;
import com.coloredcarrot.jsonapi.parsing.JsonException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReflectJsonSerializer
{
    
    public JsonNode serialize(Object toSerialize)
    {
        
        if (toSerialize == null)
            return serializeNull();
        
        if (toSerialize instanceof JsonNode)
            return (JsonNode) toSerialize;
        
        Method serializerMethod = scanForSerializerMethod(toSerialize.getClass());
        if (serializerMethod != null)
            return serializeUsingSerializer(toSerialize, serializerMethod);
        
        if (Primitives.isWrapperClass(toSerialize.getClass()) || toSerialize instanceof String)
            return serializePrimitive(toSerialize);
        if (toSerialize instanceof Collection<?>)
            return serializeCollection((Collection<?>) toSerialize);
        if (toSerialize instanceof Map<?, ?>)
            return serializeMap((Map<?, ?>) toSerialize);
        
        try
        {
            return serializeFields(toSerialize);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
        
    }
    
    protected Method scanForSerializerMethod(Class<?> clazz)
    {
        for (Method method : clazz.getDeclaredMethods())
            if (method.isAnnotationPresent(JsonSerializer.class))
                return method;
        if (clazz.getSuperclass() != null)
            return scanForSerializerMethod(clazz.getSuperclass());
        return null;
    }
    
    protected JsonNode serializeNull()
    {
        return JsonNull.INSTANCE;
    }
    
    protected JsonNode serializeUsingSerializer(Object toSerialize, Method method)
    {
        
        // Validate method signature
        if (!JsonNode.class.isAssignableFrom(method.getReturnType()))
            throw new IllegalArgumentException("JsonSerializer " + method.getName() + " has bad return type " + method.getReturnType().getName() + " (expected subtype of " + JsonNode.class.getName() + ")");
        if (method.getParameterCount() != 0)
            throw new IllegalArgumentException("JsonSerializer " + method.getName() + " has bad signature with " + method.getParameterCount() + " parameters (expected 0)");
        
        method.setAccessible(true);
        
        try
        {
            return (JsonNode) method.invoke(toSerialize);
        }
        catch (ReflectiveOperationException e)
        {
            throw new JsonException(e);
        }
        
    }
    
    protected JsonNode serializePrimitive(Object primitive)
    {
        Class<?> clazz = primitive.getClass();
        if (primitive instanceof String)
            return new JsonString((String) primitive);
        if (primitive instanceof Number)
            if (primitive instanceof Double || primitive instanceof Float)
                return new JsonDecimal(((Number) primitive).doubleValue());
            else
                return new JsonInteger(((Number) primitive).longValue());
        if (primitive instanceof Boolean)
            return new JsonBoolean((Boolean) primitive);
        if (primitive instanceof Character)
            return new JsonString(String.valueOf(primitive));
        throw new IllegalArgumentException("not a primitive: " + primitive.getClass().getName());
    }
    
    protected JsonObject serializeFields(Object toSerialize) throws IllegalAccessException
    {
        Map<String, Object> nonTransientFields = getNonTransientFields(toSerialize);
        JsonObject          out                = new JsonObject();
        nonTransientFields.forEach((name, value) -> out.add(name, serialize(value)));
        return out;
    }
    
    private Map<String, Object> getNonTransientFields(Object obj) throws IllegalAccessException
    {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields())
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
            {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        return map;
    }
    
    protected JsonNode serializeCollection(Collection<?> c)
    {
        JsonArray json = new JsonArray();
        c.forEach(item -> json.add(serialize(item)));
        return json;
    }
    
    protected JsonNode serializeMap(Map<?, ?> map)
    {
        if (map.isEmpty())
            return new JsonObject();
        // For Maps whose key is a string, we can use those as keys of a JsonObject
        if (map.keySet().iterator().next() instanceof String)
        {
            JsonObject json = new JsonObject();
            map.forEach((key, value) -> json.add((String) key, serialize(value)));
            return json;
        }
        // For Maps whose key is not a string, the return format will be
        // [[key1, value1], [key2, value2], ...]
        JsonArray json = new JsonArray();
        map.forEach((key, value) -> json.add(new JsonArray().add(serialize(key)).add(serialize(value))));
        return json;
    }
    
}
