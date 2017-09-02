package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

public class UnsafeJsonDeserializer<T>
{
    
    private final Class<T>                               clazz;
    private final BiFunction<Class<?>, JsonNode, Object> deserializerFunction;
    
    public UnsafeJsonDeserializer(Class<T> clazz, BiFunction<Class<?>, JsonNode, Object> deserializerFunction)
    {
        this.clazz = clazz;
        this.deserializerFunction = deserializerFunction;
    }
    
    @SuppressWarnings("unchecked")
    public T deserialize(JsonObject json) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        
        Unsafe unsafe = getUnsafe();
        
        T instance = (T) unsafe.allocateInstance(clazz);
        
        for (Field field : clazz.getDeclaredFields())
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
            {
                field.setAccessible(true);
                field.set(instance, deserializerFunction.apply(field.getType(), json.getOrNull(field.getName())));
            }
        
        return instance;
        
    }
    
    protected Unsafe getUnsafe() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
        unsafeConstructor.setAccessible(true);
        return unsafeConstructor.newInstance();
    }
    
}
