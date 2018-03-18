package de.znews.server.emai_reg;

import java.util.Objects;

public abstract class EmailTemplate
{
    
    public abstract String getSubject();
    
    public abstract String getPlaintext();
    
    public abstract String getHtml();
    
    public String getPlaintext(Object... parameters)
    {
        assert parameters.length % 2 == 0;
        String plaintext = getPlaintext();
        for (int i = 0; i < parameters.length; i += 2)
            plaintext = plaintext.replace(Objects.toString(parameters[i]), Objects.toString(parameters[i + 1]));
        return plaintext;
        /*Str plaintext = new Str(getPlaintext());
        for (int i = 0; i < parameters.length; i += 2)
            plaintext.replace(Objects.toString(parameters[i]), Objects.toString(parameters[i + 1]));
        return plaintext.toString();*/
    }
    
    public String getHtml(Object... parameters)
    {
        assert parameters.length % 2 == 0;
        String baseHtml = getHtml();
        if (baseHtml == null)
            return null;
        for (int i = 0; i < parameters.length; i += 2)
            baseHtml = baseHtml.replace(Objects.toString(parameters[i]), Objects.toString(parameters[i + 1]));
        return baseHtml;
        /*Str html = new Str(baseHtml);
        for (int i = 0; i < parameters.length; i += 2)
            html.replace(Objects.toString(parameters[i]), Objects.toString(parameters[i + 1]));
        return html.toString();*/
    }
    
}
