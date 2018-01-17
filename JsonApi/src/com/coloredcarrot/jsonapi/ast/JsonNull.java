package com.coloredcarrot.jsonapi.ast;

public class JsonNull extends JsonPrimitive
{
    
    private static final long serialVersionUID = 8636008688840229935L;
    
    public static final JsonNull INSTANCE = new JsonNull();
    
    private JsonNull()
    {
    }
    
    public <T> T getValue()
    {
        return null;
    }
    
    @Override
    public String stringValue()
    {
        return "null";
    }
    
    @Override
    public long longValue()
    {
        return 0L;
    }
    
    @Override
    public double doubleValue()
    {
        return 0.0D;
    }
    
    @Override
    public String toString()
    {
        return "null";
    }
    
}
