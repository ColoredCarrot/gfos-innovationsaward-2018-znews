package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;

public class IndexResource extends Resource
{
    
    public IndexResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("index.html"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        
        String template = new String(znews.staticWeb.get("index.html"));
        
        // TODO: ...
        return null;
    }
    
}
