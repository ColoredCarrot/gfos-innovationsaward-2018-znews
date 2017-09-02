package com.coloredcarrot.jsonapi.parsing;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.reflect.ReflectJsonDeserializer;

import java.io.IOException;

public class JsonInput<T> implements JsonObjectInputStream<T>
{
    
    private final JsonInputStream in;
    private final ReflectJsonDeserializer deserializer;
    private final Class<T> clazz;
    
    public JsonInput(JsonInputStream in, Class<T> clazz)
    {
        this(in, Json.getDeserializer(), clazz);
    }
    
    public JsonInput(JsonInputStream in, ReflectJsonDeserializer deserializer, Class<T> clazz)
    {
        this.in = in;
        this.deserializer = deserializer;
        this.clazz = clazz;
    }
    
    @Override
    public T read()
    {
        return deserialize(in.next());
    }
    
    public T deserialize(JsonNode jsonNode)
    {
        return deserializer.deserialize(clazz, jsonNode);
    }
    
    @Override
    public Object readFreely()
    {
        return deserializeFreely(in.next());
    }
    
    public Object deserializeFreely(JsonNode jsonNode)
    {
        return deserializer.freelyDeserialize(jsonNode);
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
    }
    
}
