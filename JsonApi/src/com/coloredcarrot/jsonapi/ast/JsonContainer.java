package com.coloredcarrot.jsonapi.ast;

import java.util.Collection;

public abstract class JsonContainer extends AbstractJsonNode
{
    
    private static final long serialVersionUID = -1162832191199680321L;
    
    public abstract Collection<JsonNode> getContents();
    
    public abstract int size();
    
    public boolean isEmpty()
    {
        return size() == 0;
    }
    
    @Override
    public String stringValue()
    {
        return String.valueOf(size());
    }
    
    @Override
    public boolean booleanValue()
    {
        return !isEmpty();
    }
    
    @Override
    public int intValue()
    {
        return size();
    }
    
    @Override
    public long longValue()
    {
        return size();
    }
    
    @Override
    public float floatValue()
    {
        return size();
    }
    
    @Override
    public double doubleValue()
    {
        return size();
    }
    
    @Override
    public byte byteValue()
    {
        return (byte) size();
    }
    
    @Override
    public short shortValue()
    {
        return (short) size();
    }
    
}
