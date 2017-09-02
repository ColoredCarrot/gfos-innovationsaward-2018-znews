package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonNode;

import java.util.function.BiFunction;

public class UnsafeJsonDeserializerFactory
{
    
    private Boolean unsafeAvailable = null;
    
    public <T> UnsafeJsonDeserializer<T> getOrNull(Class<T> clazz, BiFunction<Class<?>, JsonNode, Object> deserializerFunction)
    {
        return isUnsafeAvailable() ? new UnsafeJsonDeserializer<T>(clazz, deserializerFunction) : null;
    }
    
    public boolean isUnsafeAvailable()
    {
        if (unsafeAvailable == null)
            computeUnsafeAvailable();
        return unsafeAvailable;
    }
    
    private void computeUnsafeAvailable()
    {
        try
        {
            Class.forName("sun.misc.Unsafe");
            unsafeAvailable = true;
        }
        catch (ClassNotFoundException e)
        {
            unsafeAvailable = false;
        }
    }
    
}
