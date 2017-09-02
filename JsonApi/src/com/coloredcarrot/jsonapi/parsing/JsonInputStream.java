package com.coloredcarrot.jsonapi.parsing;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;

import java.io.Closeable;

public abstract class JsonInputStream implements Closeable
{
    
    protected abstract JsonNode read();
    
    public JsonNode next()
    {
        return read();
    }
    
    public JsonObject parse()
    {
        return (JsonObject) next();
    }
    
}
