package com.coloredcarrot.jsonapi.parsing.lex;

import java.io.Reader;

public class JsonLexer extends AbstractJsonLexer
{
    
    public JsonLexer(Reader in)
    {
        super(in);
    }
    
    @Override
    protected JsonToken read()
    {
        
        switch (readIn())
        {
        case -1:
            return JsonToken.EOF;
        case '[':
            return JsonToken.ARRAY_START;
        case ']':
            return JsonToken.ARRAY_END;
        case '{':
            return JsonToken.OBJECT_START;
        case '}':
            return JsonToken.OBJECT_END;
        case ':':
            return JsonToken.COLON;
        case ',':
            return JsonToken.COMMA;
        case 't':
            return readTrue();
        case 'f':
            return readFalse();
        case 'n':
            return readNull();
        case '"':
            return readString();
        default:
            if (currentIn() >= '0' && currentIn() <= '9' || currentIn() == '+' || currentIn() == '-' || currentIn() == '.')
                return readNumber();
            return JsonToken.INVALID_TOKEN;
        }
        
    }
    
    protected JsonToken readTrue()
    {
        requireIn('r');
        requireIn('u');
        requireIn('e');
        return JsonToken.BOOLEAN_TRUE;
    }
    
    protected JsonToken readFalse()
    {
        requireIn('a');
        requireIn('l');
        requireIn('s');
        requireIn('e');
        return JsonToken.BOOLEAN_FALSE;
    }
    
    protected JsonToken readNull()
    {
        requireIn('u');
        requireIn('l');
        requireIn('l');
        return JsonToken.NULL;
    }
    
    protected JsonToken readString()
    {
        // We've already read the leading "
        
        StringBuilder sb = new StringBuilder();
        
        // Read until EOF or trailing "
        while (readIn() != -1 && currentIn() != '"')
        {
            char c = (char) currentIn();
            
            // c may be a backslash to escape stuff
            if (c == '\\')
            {
                // Escape immediately followed by EOF
                if (readIn() == -1)
                    return JsonToken.INVALID_TOKEN;
                c = (char) currentIn();
                
                switch (c)
                {
                // List of escapable characters; see https://www.ietf.org/rfc/rfc4627.txt Pages 3-4
                case '\\':
                case '"':
                case '/':
                    sb.append(c);
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'u':  // I checked; the u MUST be lowercase according to RFC 4627
                    int escaped = readEscapedSequenceInString();
                    if (escaped == -1)  // Returns -1 to indicate an error
                        return JsonToken.INVALID_TOKEN;
                    sb.append((char) escaped);
                    break;
                default:
                    return JsonToken.INVALID_TOKEN;
                }
                
                continue;
                
            }
            
            sb.append(c);
        }
        
        return JsonToken.string(sb.toString());
        
    }
    
    protected int readEscapedSequenceInString()
    {
        // Right now, we're directly after \\u
        // We need now read 4 characters
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; ++i)
        {
            if (readIn() == -1)
                return -1;
            sb.append((char) currentIn());
        }
        // This is a cool way to convert the string "XXXX" to the char '\\uXXXX'
        return (char) Integer.parseInt(sb.toString(), 16);
    }
    
    protected JsonToken readNumber()
    {
        // We've already read the first digit (or symbol)
        
        boolean       seenDot = false;
        boolean       seenE   = false;
        StringBuilder sb      = new StringBuilder();
    
        if (currentIn() == '+' || currentIn() == '-')
        {
            sb.append((char) currentIn());
            if (readIn() == -1)
                return JsonToken.INVALID_TOKEN;
        }
        
        do
        {
            sb.append((char) currentIn());
        }
        while (readIn() != -1 && (currentIn() >= '0' && currentIn() <= '9'  // Is [0-9]
                || !seenE && (seenE = currentIn() == 'e' || currentIn() == 'E')  // Is first [eE]
                || !seenDot && !seenE && (seenDot = currentIn() == '.')));  // Is first [.,] if not seen [eE]
    
        // Push back last read non-number-symbol
        pushBack();
    
        // If E is the last char, that is invalid (an exponent number MUST follow)
        if (seenE)
        {
            char lastChar = sb.charAt(sb.length() - 1);
            if (lastChar == 'e' || lastChar == 'E')
                return JsonToken.INVALID_TOKEN;
        }
        
        if (seenDot)
            return JsonToken.decimal(Double.parseDouble(sb.toString()));
        return JsonToken.integer(Long.parseLong(sb.toString()));
        
    }
    
}
