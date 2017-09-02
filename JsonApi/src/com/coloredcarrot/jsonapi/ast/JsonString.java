package com.coloredcarrot.jsonapi.ast;

import org.jetbrains.annotations.NotNull;

public class JsonString extends JsonPrimitive
{
    
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
    
}
