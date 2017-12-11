package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import de.znews.server.util.Str;

@Deprecated
public class IndexResource_OLD extends Resource
{
    
    public IndexResource_OLD(ZNews znews)
    {
        super(znews, URIFragment.fromURI("index.html_old"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
    
        Str articleTemplate = new Str(znews.staticWeb.getString("article.html"));
    
        int artTempTitleStart = articleTemplate.indexOf("%%__headline__%%");
        int artTempTitleLen = "%%__headline__%%".length();
        
        // TODO: Make amount of displayed newsletters configurable
        Iterable<Newsletter> latestNewsletters = znews.newsletterManager.getLatestNewsletters(5);
    
        Str articles = new Str(articleTemplate.length() * 5 + 500 * 5);
    
        for (Newsletter newsletter : latestNewsletters)
        {
            Str articleStr = articleTemplate.copy()
                                            .setChars(artTempTitleStart, artTempTitleLen, newsletter.getTitle())
                                            .replaceOnce("%%__text__%%", newsletter.getText());
            articles.append(articleStr);
        }
        
        Str template = new Str(znews.staticWeb.getString("index.html"));
        
        template.replaceOnce("%%__articles__%%".toCharArray(), articles.getBuffer());
    
        return RequestResponse.ok(template.toString());
        
    }
    
}
