package com.coloredcarrot.jsonapi.parsing.lex;

public enum JsonTokenType
{
    
    EOF,
    INVALID_TOKEN,
    ARRAY_START,
    ARRAY_END,
    OBJECT_START,
    OBJECT_END,
    COLON,
    COMMA,
    INTEGER,
    DECIMAL,
    STRING,
    BOOLEAN,
    NULL;
    
}
