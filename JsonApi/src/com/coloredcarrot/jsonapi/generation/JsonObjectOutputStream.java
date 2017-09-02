package com.coloredcarrot.jsonapi.generation;

import java.io.Closeable;

public interface JsonObjectOutputStream extends Closeable
{
    
    void write(Object obj);
    
}
