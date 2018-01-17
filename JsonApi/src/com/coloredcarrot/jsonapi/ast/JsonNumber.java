package com.coloredcarrot.jsonapi.ast;

public abstract class JsonNumber extends JsonPrimitive
{
    
    private static final long serialVersionUID = -1158799219649760631L;
    
    public abstract Number getNumber();
    
    @Override
    public String toString()
    {
        return getNumber().toString();
    }
    
}
