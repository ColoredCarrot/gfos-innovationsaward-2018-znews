package com.coloredcarrot.jsonapi.parsing.lex;

import com.coloredcarrot.jsonapi.parsing.JsonException;

import java.io.IOException;
import java.io.Reader;

public abstract class AbstractJsonLexer extends JsonTokenInputStream
{
    
    private final Reader in;
    
    private int current = -2;
    private int lookahead = -2;
    
    public AbstractJsonLexer(Reader in)
    {
        this.in = in;
    }
    
    protected int readIn()
    {
        if (lookahead != -2)
        {
            int lookahead = this.lookahead;
            this.lookahead = -2;
            return current = lookahead;
        }
        return current = readIn0();
    }
    
    protected int readIn0()
    {
        try
        {
            int read;
            while ((read = in.read()) != -1 && Character.isWhitespace(read));
            return read;
        }
        catch (IOException e)
        {
            // TODO: Implement proper error handling
            e.printStackTrace();
            return -1;
        }
    }
    
    protected int currentIn()
    {
        return current;
    }
    
    protected int peekIn()
    {
        if (lookahead == -2)
            lookahead = readIn0();
        return lookahead;
    }
    
    /* Unsafe */
    protected void pushBack()
    {
        lookahead = current;
    }
    
    protected void requireIn(int c)
    {
        if (readIn() != c)
            throw new JsonException();
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
    }
    
}
