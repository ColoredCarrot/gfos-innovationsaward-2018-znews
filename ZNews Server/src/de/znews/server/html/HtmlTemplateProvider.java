package de.znews.server.html;

import de.znews.server.ZNews;
import de.znews.server.static_web.StaticWeb;
import de.znews.server.util.Str;

import java.nio.charset.StandardCharsets;

public class HtmlTemplateProvider
{
    
    private final StaticWeb staticWeb;
    
    public HtmlTemplateProvider(ZNews znews)
    {
        staticWeb = znews.staticWeb;
    }
    
    public HtmlTemplate getTemplate(String path)
    {
        return new HtmlTemplate(new Str(new String(staticWeb.get(path), StandardCharsets.UTF_8)));
    }
    
}
