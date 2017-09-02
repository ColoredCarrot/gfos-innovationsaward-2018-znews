package com.coloredcarrot.jsonapi.ast;

public abstract class AbstractJsonNode extends Number implements JsonNode
{
    
    @Override
    public boolean booleanValue()
    {
        switch (stringValue().toLowerCase())
        {
        case "true":
        case "yes":
        case "ok":
        case "accept":
            return true;
        default:
            return doubleValue() >= 1.0D;
        }
    }
    
    @Override
    public int intValue()
    {
        return (int) doubleValue();
    }
    
    @Override
    public long longValue()
    {
        try
        {
            return Long.parseLong(stringValue());
        }
        catch (NumberFormatException e)
        {
            return (long) doubleValue();
        }
    }
    
    @Override
    public float floatValue()
    {
        return (float) doubleValue();
    }
    
    @Override
    public double doubleValue()
    {
        try
        {
            return Double.parseDouble(stringValue());
        }
        catch (NumberFormatException e)
        {
            return 0.0D;
        }
    }
    
    @Override
    public byte byteValue()
    {
        return (byte) intValue();
    }
    
    @Override
    public short shortValue()
    {
        return (short) intValue();
    }
    
}
