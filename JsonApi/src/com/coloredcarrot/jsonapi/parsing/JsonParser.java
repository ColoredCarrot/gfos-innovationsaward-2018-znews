package com.coloredcarrot.jsonapi.parsing;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;
import com.coloredcarrot.jsonapi.parsing.lex.JsonTokenInputStream;
import com.coloredcarrot.jsonapi.parsing.lex.JsonTokenType;

import java.io.IOException;

public class JsonParser extends JsonInputStream
{
    
    private final JsonTokenInputStream in;
    
    public JsonParser(JsonTokenInputStream in)
    {
        this.in = in;
    }
    
    @Override
    protected JsonNode read()
    {
        switch (in.lookahead().getType())
        {
        case EOF:
            return null;
        case STRING:
            return readString();
        case INTEGER:
            return readInteger();
        case DECIMAL:
            return readDecimal();
        case BOOLEAN:
            return readBoolean();
        case NULL:
            return readNull();
        case OBJECT_START:
            return readObject();
        case ARRAY_START:
            return readArray();
        default:
            throw new JsonException("Unexpected token: " + in.next().getType());
        }
    }
    
    protected JsonString readString()
    {
        return new JsonString(in.next().getValueAsString());
    }
    
    protected JsonInteger readInteger()
    {
        return new JsonInteger(in.next().getValueAsLong());
    }
    
    protected JsonDecimal readDecimal()
    {
        return new JsonDecimal(in.next().getValueAsDouble());
    }
    
    protected JsonBoolean readBoolean()
    {
        return new JsonBoolean(in.next().getValueAsBoolean());
    }
    
    protected JsonNull readNull()
    {
        in.next();
        return JsonNull.INSTANCE;
    }
    
    protected JsonObject readObject()
    {
        // Read leading OBJECT_START
        in.require(JsonTokenType.OBJECT_START);
    
        JsonObject result = new JsonObject();
        
        if (in.lookahead().getType() == JsonTokenType.OBJECT_END)
        {
            in.next();
            return result;
        }
        
        do
        {
            String key = in.requireString();
            in.require(JsonTokenType.COLON);
            result.add(key, read());
        }
        while (in.next().getType() == JsonTokenType.COMMA);
        
        if (in.current().getType() != JsonTokenType.OBJECT_END)
            // Missing trailing }
            throw new JsonException("Missing closing curly parentheses; instead found " + in.current().getType());
    
        return result;
    
    }
    
    protected JsonArray readArray()
    {
        // Read leading ARRAY_START
        in.require(JsonTokenType.ARRAY_START);
    
        JsonArray result = new JsonArray();
    
        if (in.lookahead().getType() == JsonTokenType.ARRAY_END)
        {
            in.next();
            return result;
        }
        
        do
        {
            result.add(read());
        }
        while (in.next().getType() == JsonTokenType.COMMA);
    
        if (in.current().getType() != JsonTokenType.ARRAY_END)
            // Missing trailing }
            throw new JsonException("Missing closing brackets; instead found " + in.current().getType());
    
        return result;
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
    }
    
}
