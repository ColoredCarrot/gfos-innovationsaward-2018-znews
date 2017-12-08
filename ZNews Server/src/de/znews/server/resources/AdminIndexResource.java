package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import de.znews.server.util.Str;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public class AdminIndexResource extends Resource
{
    
    public AdminIndexResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("admin/index"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        
        Str template = new Str(znews.staticWeb.getString("index.html"));
    
        RequestResponse resp = RequestResponse.ok(template.toString());
        resp.addCookie(new DefaultCookie("znews_auth", znews.sessionManager.authenticate("coloredc", "root")));
        return resp;
        
    }
    
}
