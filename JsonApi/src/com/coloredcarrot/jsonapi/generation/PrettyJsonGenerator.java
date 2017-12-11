package com.coloredcarrot.jsonapi.generation;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

public class PrettyJsonGenerator extends AbstractJsonGenerator
{
    
    private final Writer  out;
    private final String  tabCharacter;
    private final boolean keepSimpleArraysOnOneLine;
    
    private int indentationLevel;
    
    public PrettyJsonGenerator(Writer out)
    {
        this(out, "\t", true);
    }
    
    public PrettyJsonGenerator(Writer out, String tabCharacter, boolean keepSimpleArraysOnOneLine)
    {
        this.out = out;
        this.tabCharacter = tabCharacter;
        this.keepSimpleArraysOnOneLine = keepSimpleArraysOnOneLine;
    }
    
    @Override
    protected void writeRaw(String rawString)
    {
        try
        {
            out.write(rawString);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    protected void writeObject(JsonObject object, StringBuilder out)
    {
        out.append('{');
        signalIndent();
        for (Map.Entry<String, JsonNode> mapping : object.getMappings().entrySet())
        {
            beginNewLine(out);
            out.append('"').append(mapping.getKey()).append("\": ");
            write(mapping.getValue(), out);
            out.append(',');
        }
        // Delete last comma (,)
        if (!object.isEmpty())
            out.deleteCharAt(out.length() - 1);
        signalUnindent();
        beginNewLine(out);
        out.append('}');
    }
    
    @Override
    protected void writeArray(JsonArray array, StringBuilder out)
    {
        if (keepSimpleArraysOnOneLine && array.isSimpleArray())
            writeSmallPrimitiveArray(array, out);
        else
            writeComplexArray(array, out);
    }
    
    protected void writeComplexArray(JsonArray array, StringBuilder out)
    {
        out.append('[');
        signalIndent();
        for (JsonNode element : array.getContentsAsArray())
        {
            beginNewLine(out);
            write(element, out);
            out.append(',');
        }
        // Delete last comma (,)
        if (!array.isEmpty())
            out.deleteCharAt(out.length() - 1);
        signalUnindent();
        beginNewLine(out);
        out.append(']');
    }
    
    protected void writeSmallPrimitiveArray(JsonArray array, StringBuilder out)
    {
        out.append('[');
        for (JsonNode element : array.getContentsAsArray())
        {
            write(element, out);
            out.append(", ");
        }
        // Delete last comma (,)
        if (!array.isEmpty())
            out.delete(out.length() - 2, out.length());
        out.append(']');
    }
    
    @Override
    protected void writeString(JsonString string, StringBuilder out)
    {
        out.append('"').append(string.getValue()).append('"');
    }
    
    @Override
    protected void writeInteger(JsonInteger integer, StringBuilder out)
    {
        out.append(integer.getValue());
    }
    
    @Override
    protected void writeDecimal(JsonDecimal decimal, StringBuilder out)
    {
        out.append(decimal.getValue());
    }
    
    @Override
    protected void writeBoolean(JsonBoolean bool, StringBuilder out)
    {
        out.append(bool.getValue());
    }
    
    @Override
    protected void writeNull(JsonNull nullNode, StringBuilder out)
    {
        out.append("null");
    }
    
    @Override
    public void close() throws IOException
    {
        out.close();
    }
    
    private void beginNewLine(StringBuilder out)
    {
        out.append('\n');
        for (int i = 0; i < indentationLevel; i++)
            out.append(tabCharacter);
    }
    
    private void signalIndent()
    {
        ++indentationLevel;
    }
    
    private void signalUnindent()
    {
        --indentationLevel;
    }
    
}
