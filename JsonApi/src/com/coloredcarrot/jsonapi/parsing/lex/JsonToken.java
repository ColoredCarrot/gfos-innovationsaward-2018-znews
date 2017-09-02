package com.coloredcarrot.jsonapi.parsing.lex;

public class JsonToken
{
    
    public static final JsonToken EOF           = of(JsonTokenType.EOF);
    public static final JsonToken INVALID_TOKEN = of(JsonTokenType.INVALID_TOKEN);
    public static final JsonToken ARRAY_START   = of(JsonTokenType.ARRAY_START);
    public static final JsonToken ARRAY_END     = of(JsonTokenType.ARRAY_END);
    public static final JsonToken OBJECT_START  = of(JsonTokenType.OBJECT_START);
    public static final JsonToken OBJECT_END    = of(JsonTokenType.OBJECT_END);
    public static final JsonToken COLON         = of(JsonTokenType.COLON);
    public static final JsonToken COMMA         = of(JsonTokenType.COMMA);
    public static final JsonToken NULL          = of(JsonTokenType.NULL);
    
    public static final JsonToken BOOLEAN_TRUE  = of(JsonTokenType.BOOLEAN, true);
    public static final JsonToken BOOLEAN_FALSE = of(JsonTokenType.BOOLEAN, false);
    
    public static JsonToken integer(long value)
    {
        return of(JsonTokenType.INTEGER, value);
    }
    
    public static JsonToken decimal(double value)
    {
        return of(JsonTokenType.DECIMAL, value);
    }
    
    public static JsonToken string(String value)
    {
        return of(JsonTokenType.STRING, value);
    }
    
    public static JsonToken bool(boolean value)
    {
        return value ? BOOLEAN_TRUE : BOOLEAN_FALSE;
    }
    
    private static JsonToken of(JsonTokenType type, Object value)
    {
        return new JsonToken(type, value);
    }
    
    private static JsonToken of(JsonTokenType type)
    {
        return new JsonToken(type, null);
    }
    
    private final JsonTokenType type;
    private final Object        value;
    
    private JsonToken(JsonTokenType type, Object value)
    {
        this.type = type;
        this.value = value;
    }
    
    public JsonTokenType getType()
    {
        return type;
    }
    
    public Object getValue()
    {
        return value;
    }
    
    public String getValueAsString()
    {
        return (String) getValue();
    }
    
    public Long getValueAsLong()
    {
        return (Long) getValue();
    }
    
    public Double getValueAsDouble()
    {
        return (Double) getValue();
    }
    
    public Boolean getValueAsBoolean()
    {
        return (Boolean) getValue();
    }
    
}
