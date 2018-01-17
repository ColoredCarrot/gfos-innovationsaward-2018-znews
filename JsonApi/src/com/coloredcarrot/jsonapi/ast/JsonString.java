package com.coloredcarrot.jsonapi.ast;

import org.jetbrains.annotations.NotNull;

public class JsonString extends JsonPrimitive
{
    
    private static final long serialVersionUID = -2204799298953401168L;
    
    @NotNull private String value;
    
    public JsonString(@NotNull String value)
    {
        this.value = value;
    }
    
    @NotNull
    public String getValue()
    {
        return value;
    }
    
    public String setValue(String newValue)
    {
        String oldValue = value;
        value = newValue;
        return oldValue;
    }
    
    @Override
    public String stringValue()
    {
        return value;
    }
    
    @Override
    public String toString()
    {
        return "\"" + value + "\"";
    }
    
}
