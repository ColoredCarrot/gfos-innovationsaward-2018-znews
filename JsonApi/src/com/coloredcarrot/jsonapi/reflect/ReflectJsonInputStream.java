package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.parsing.JsonInputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ReflectJsonInputStream extends JsonInputStream
{
    
    private final Queue<Object>         objectsToSerialize;
    private final ReflectJsonSerializer serializer;
    private boolean closed;
    
    public ReflectJsonInputStream(Queue<Object> objectsToSerialize, ReflectJsonSerializer serializer)
    {
        this.objectsToSerialize = objectsToSerialize;
        this.serializer = serializer;
    }
    
    public ReflectJsonInputStream(ReflectJsonSerializer serializer, Object... objectsToSerialize)
    {
        this.serializer = serializer;
        this.objectsToSerialize = new LinkedList<>(Arrays.asList(objectsToSerialize));
    }
    
    @Override
    protected JsonNode read()
    {
        return serializeNext();
    }
    
    protected JsonNode serializeNext()
    {
        ensureNotClosed();
        return serialize(objectsToSerialize.remove());
    }
    
    protected JsonNode serialize(Object obj)
    {
        return serializer.serialize(obj);
    }
    
    @Override
    public void close() throws IOException
    {
        if (closed)
            return;
        objectsToSerialize.clear();
        closed = true;
    }
    
    private void ensureNotClosed()
    {
        if (closed)
            throw new IllegalStateException("Stream closed");
    }
    
}
