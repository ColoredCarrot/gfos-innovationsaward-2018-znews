package com.coloredcarrot.jsonapi.parsing;

import java.io.Closeable;

public interface JsonObjectInputStream<T> extends Closeable
{
    
    T read();
    
    Object readFreely();
    
}
