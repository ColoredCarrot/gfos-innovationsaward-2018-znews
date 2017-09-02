package com.coloredcarrot.jsonapi.ast;

public class JsonInteger extends JsonNumber
{
    
    private long value;
    
    public JsonInteger(long value)
    {
        this.value = value;
    }
    
    public long getValue()
    {
        return value;
    }
    
    public long setValue(long newValue)
    {
        long oldValue = value;
        value = newValue;
        return oldValue;
    }
    
    @Override
    public Number getNumber()
    {
        return value;
    }
    
    @Override
    public int intValue()
    {
        return (int) value;
    }
    
    @Override
    public String stringValue()
    {
        return String.valueOf(value);
    }
    
    @Override
    public double doubleValue()
    {
        return value;
    }
    
    @Override
    public long longValue()
    {
        return value;
    }
    
}
