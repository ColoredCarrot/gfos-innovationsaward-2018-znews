package de.znews.server.uri;

import de.znews.server.resources.Params;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class URIQuery
{
    
    public static final URIQuery EMPTY = new URIQuery();
    
    public static URIQuery fromString(String queryString)
    {
        return new URIQuery(queryString);
    }
    
    @Getter
    private final Map<String, String> params;
    
    public URIQuery(Map<String, String> params)
    {
        this.params = new HashMap<>(params);
    }
    
    private URIQuery(String s)
    {
        
        params = new HashMap<>();
        
        if (s.startsWith("?"))
            s = s.substring(1);
        
        StringBuilder sb = new StringBuilder();
        
        char[] c = s.toCharArray();
        int    i = 0;
        
        while (i < c.length)
        {
            
            StringBuilder nameBuilder = new StringBuilder();
            
            char nameChar = 0;
            while (i < c.length && (nameChar = c[i++]) != '=' && nameChar != '&')
                nameBuilder.append(nameChar);
            
            if (nameChar == '=')
            {
                StringBuilder valueBuilder = new StringBuilder();
                char          valueChar;
                while (i < c.length && (valueChar = c[i++]) != '&')
                    valueBuilder.append(valueChar);
                params.put(nameBuilder.toString(), valueBuilder.toString());
            }
            else
                params.put(nameBuilder.toString(), "");
            
        }
        
    }
    
    private URIQuery()
    {
        params = Collections.emptyMap();
    }
    
    @Override
    public String toString()
    {
        StringJoiner sj = new StringJoiner("&");
        params.forEach((key, value) -> sj.add(value.trim().isEmpty() ? key : key + '=' + value));
        return sj.toString();
    }
    
    public URIQuery withParams(Map<String, String> paramAssignments)
    {
        
        Map<String, String> params = new HashMap<>(this.params);
        
        paramAssignments.forEach((paramKey, paramValue) ->
        {
            if (params.containsKey(paramKey))
                params.put(paramKey, paramValue);
        });
        
        return new URIQuery(params);
        
    }
    
    public Params toParams()
    {
        return new Params(params);
    }
    
}
