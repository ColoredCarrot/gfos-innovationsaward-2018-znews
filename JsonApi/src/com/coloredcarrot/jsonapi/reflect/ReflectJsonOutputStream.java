package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.generation.JsonOutputStream;

import java.util.function.Consumer;

public class ReflectJsonOutputStream<T> implements JsonOutputStream
{
    
    private final Class<T>                clazz;
    private final ReflectJsonDeserializer deserializer;
    private final Consumer<? super T>     writeConsumer;
    
    private boolean isClosed;
    private T lastResult;
    
    public ReflectJsonOutputStream(Class<T> clazz, ReflectJsonDeserializer deserializer, Consumer<? super T> writeConsumer)
    {
        this.clazz = clazz;
        this.deserializer = deserializer;
        this.writeConsumer = writeConsumer;
    }
    
    @Override
    public void write(JsonNode node)
    {
        if (isClosed)
            throw new IllegalStateException("Stream closed");
        deserialize(node);
        if (writeConsumer != null)
            writeConsumer.accept(lastResult);
    }
    
    public T deserialize(JsonNode node)
    {
        return lastResult = deserializer.deserialize(clazz, node);
    }
    
    public T getLastResult()
    {
        return lastResult;
    }
    
    @Override
    public void close()
    {
        isClosed = true;
    }
    
}
