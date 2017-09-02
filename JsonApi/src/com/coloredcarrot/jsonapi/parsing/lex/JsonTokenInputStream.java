package com.coloredcarrot.jsonapi.parsing.lex;

import com.coloredcarrot.jsonapi.parsing.JsonException;

import java.io.Closeable;

public abstract class JsonTokenInputStream implements Closeable
{
    
    private boolean   reachedEOF;
    private JsonToken current;
    private JsonToken lookahead;
    
    protected abstract JsonToken read();
    
    // Since the behaviour for read() after EOF is undefined, we
    // implement a fail-safe to return null after the first EOF token
    protected JsonToken readOrNull()
    {
        if (reachedEOF)
            return null;
        JsonToken result = read();
        if (result.getType() == JsonTokenType.EOF)
            reachedEOF = true;
        return result;
    }
    
    public JsonToken lookahead()
    {
        if (lookahead == null)
            lookahead = readOrNull();
        return lookahead;
    }
    
    public JsonToken next()
    {
        return current = next0();
    }
    
    protected JsonToken next0()
    {
        if (lookahead != null)
        {
            JsonToken lookahead = this.lookahead;
            this.lookahead = null;
            return lookahead;
        }
        return read();
    }
    
    public JsonToken current()
    {
        return current;
    }
    
    public Object require(JsonTokenType type)
    {
        if (next().getType() != type)
            throw new JsonException();
        return current().getValue();
    }
    
    public String requireString()
    {
        return (String) require(JsonTokenType.STRING);
    }
    
}
