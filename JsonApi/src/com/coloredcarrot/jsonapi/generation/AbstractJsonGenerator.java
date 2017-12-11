package com.coloredcarrot.jsonapi.generation;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;

public abstract class AbstractJsonGenerator implements JsonOutputStream
{
    
    protected abstract void writeRaw(String rawString);
    
    @Override
    public void write(JsonNode node)
    {
        StringBuilder out = new StringBuilder();
        write(node, out);
        writeRaw(out.toString());
    }
    
    protected void write(JsonNode node, StringBuilder out)
    {
        if (node instanceof JsonObject)
            writeObject((JsonObject) node, out);
        else if (node instanceof JsonArray)
            writeArray((JsonArray) node, out);
        else if (node instanceof JsonString)
            writeString((JsonString) node, out);
        else if (node instanceof JsonInteger)
            writeInteger((JsonInteger) node, out);
        else if (node instanceof JsonDecimal)
            writeDecimal((JsonDecimal) node, out);
        else if (node instanceof JsonBoolean)
            writeBoolean((JsonBoolean) node, out);
        else if (node instanceof JsonNull)
            writeNull((JsonNull) node, out);
    }
    
    protected abstract void writeObject(JsonObject object, StringBuilder out);
    
    protected abstract void writeArray(JsonArray array, StringBuilder out);
    
    /*
     * Implementations are responsible for escaping the string
     */
    protected abstract void writeString(JsonString string, StringBuilder out);
    
    protected abstract void writeInteger(JsonInteger integer, StringBuilder out);
    
    protected abstract void writeDecimal(JsonDecimal decimal, StringBuilder out);
    
    protected abstract void writeBoolean(JsonBoolean bool, StringBuilder out);
    
    protected abstract void writeNull(JsonNull nullNode, StringBuilder out);
    
}
