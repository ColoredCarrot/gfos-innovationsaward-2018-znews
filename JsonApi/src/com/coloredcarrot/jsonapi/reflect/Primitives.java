package com.coloredcarrot.jsonapi.reflect;

import java.util.HashMap;
import java.util.Map;

public class Primitives
{
    
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();
    static
    {
        WRAPPER_TO_PRIMITIVE.put(Byte.class, byte.class);
        WRAPPER_TO_PRIMITIVE.put(Short.class, short.class);
        WRAPPER_TO_PRIMITIVE.put(Integer.class, int.class);
        WRAPPER_TO_PRIMITIVE.put(Long.class, long.class);
        WRAPPER_TO_PRIMITIVE.put(Float.class, float.class);
        WRAPPER_TO_PRIMITIVE.put(Double.class, double.class);
        WRAPPER_TO_PRIMITIVE.put(Boolean.class, boolean.class);
        WRAPPER_TO_PRIMITIVE.put(Character.class, char.class);
        WRAPPER_TO_PRIMITIVE.put(Void.class, void.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(void.class, Void.class);
    }
    
    public static boolean isWrapperClass(Class<?> clazz)
    {
        return WRAPPER_TO_PRIMITIVE.containsKey(clazz);
    }
    
    public static boolean isPrimitiveClass(Class<?> clazz)
    {
        return PRIMITIVE_TO_WRAPPER.containsKey(clazz);
    }
    
    public static Class<?> getPrimitiveClass(Class<?> wrapperClass)
    {
        return WRAPPER_TO_PRIMITIVE.get(wrapperClass);
    }
    
    public static Class<?> getWrapperClass(Class<?> primitiveClass)
    {
        return PRIMITIVE_TO_WRAPPER.get(primitiveClass);
    }
    
    public static boolean isPrimitiveOrWrapperClass(Class<?> clazz)
    {
        return isWrapperClass(clazz) || isPrimitiveClass(clazz);
    }
    
    public static Class<?> toPrimitiveClass(Class<?> primitiveOrWrapperClass)
    {
        return WRAPPER_TO_PRIMITIVE.getOrDefault(primitiveOrWrapperClass, primitiveOrWrapperClass);
    }
    
    public static Class<?> toWrapperClass(Class<?> primitiveOrWrapperClass)
    {
        return PRIMITIVE_TO_WRAPPER.getOrDefault(primitiveOrWrapperClass, primitiveOrWrapperClass);
    }
    
}
