package com.coloredcarrot.jsonapi.parsing;

public class JsonException extends RuntimeException
{
    
    private static final long serialVersionUID = 5037221029660388634L;
    
    public JsonException()
    {
        super("JSON could not be parsed");
    }
    
    public JsonException(String message)
    {
        super(message);
    }
    
    public JsonException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public JsonException(Throwable cause)
    {
        super(cause);
    }
    
}
