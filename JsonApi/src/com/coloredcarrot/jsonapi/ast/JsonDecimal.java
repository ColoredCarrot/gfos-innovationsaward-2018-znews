package com.coloredcarrot.jsonapi.ast;

public class JsonDecimal extends JsonNumber
{
    
    private static final long serialVersionUID = -5926416609006074371L;
    
    private double value;
    
    public JsonDecimal(double value)
    {
        this.value = value;
    }
    
    public double getValue()
    {
        return value;
    }
    
    public double setValue(double newValue)
    {
        double oldValue = value;
        value = newValue;
        return oldValue;
    }
    
    @Override
    public Number getNumber()
    {
        return value;
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
        return (long) value;
    }
    
    @Override
    public String toString()
    {
        return Double.toString(value);
    }
    
}
