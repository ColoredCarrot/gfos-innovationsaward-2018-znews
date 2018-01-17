package com.coloredcarrot.jsonapi.ast;

public class JsonBoolean extends JsonPrimitive
{
    
    private static final long serialVersionUID = 345704022643295159L;
    
    private boolean value;
    
    public JsonBoolean(boolean value)
    {
        this.value = value;
    }
    
    public boolean getValue()
    {
        return value;
    }
    
    public boolean setValue(boolean newValue)
    {
        boolean oldValue = value;
        value = newValue;
        return oldValue;
    }
    
    @Override
    public String stringValue()
    {
        return Boolean.toString(value);
    }
    
    @Override
    public boolean booleanValue()
    {
        return value;
    }
    
    @Override
    public long longValue()
    {
        return value ? 1L : 0L;
    }
    
    @Override
    public double doubleValue()
    {
        return value ? 1.0D : 0.0D;
    }
    
    @Override
    public String toString()
    {
        return value ? "true" : "false";
    }
    
}
