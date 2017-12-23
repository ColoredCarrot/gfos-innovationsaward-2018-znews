package de.znews.server.resources.admin;

import de.znews.server.ZNews;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.RequestResponse;
import de.znews.server.resources.Resource;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.sessions.Session;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.nio.charset.StandardCharsets;

/*
Parameters:

POST: email, password
 */
public class GetTokenResource extends Resource
{
    
    public GetTokenResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("admin/api/get_token"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        if (!ctx.hasPostParam("email") || !ctx.hasPostParam("password"))
            throw new Http400BadRequestException();
        
        String token = znews.sessionManager.authenticate(ctx.getStringPostParam("email"), ctx.getStringPostParam("password"))
                                           .map(Session::getToken)
                                           .orElseThrow(() -> new Http403ForbiddenException("Invalid Credentials"));
        
        RequestResponse resp       = new RequestResponse(token.getBytes(StandardCharsets.UTF_8));
        DefaultCookie   authCookie = new DefaultCookie("znews_auth", token);
        authCookie.setMaxAge(Cookie.UNDEFINED_MAX_AGE);
        resp.addCookie(authCookie);
        return resp;
        
    }
    
}
