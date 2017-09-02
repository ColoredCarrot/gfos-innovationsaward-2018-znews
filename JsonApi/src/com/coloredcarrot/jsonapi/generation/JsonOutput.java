package com.coloredcarrot.jsonapi.generation;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.reflect.ReflectJsonSerializer;

import java.io.IOException;

public class JsonOutput implements JsonObjectOutputStream, JsonOutputStream
{
    
    private final JsonOutputStream      out;
    private final ReflectJsonSerializer serializer;
    
    public JsonOutput(JsonOutputStream out)
    {
        this(out, Json.getSerializer());
    }
    
    public JsonOutput(JsonOutputStream out, ReflectJsonSerializer serializer)
    {
        this.out = out;
        this.serializer = serializer;
    }
    
    @Override
    public void write(JsonNode node)
    {
        out.write(node);
    }
    
    @Override
    public void write(Object obj)
    {
        write(serialize(obj));
    }
    
    public JsonNode serialize(Object obj)
    {
        return serializer.serialize(obj);
    }
    
    @Override
    public void close() throws IOException
    {
        out.close();
    }
    
}
