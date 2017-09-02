package com.coloredcarrot.jsonapi.generation;

import com.coloredcarrot.jsonapi.ast.JsonNode;

import java.io.Closeable;

public interface JsonOutputStream extends Closeable
{
    
    void write(JsonNode node);
    
}
