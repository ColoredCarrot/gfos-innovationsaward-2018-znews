package de.znews.server.resources.admin;

import de.znews.server.ZNews;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.RequestResponse;
import de.znews.server.resources.Resource;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.nio.charset.StandardCharsets;

/*
Parameters:

POST: username, password
 */
public class GetTokenResource extends Resource
{
	
	public GetTokenResource(ZNews znews)
	{
		super(znews, URIFragment.fromURI("api/v1/admin/get_token"));
	}
	
	@Override
	public RequestResponse handleRequest(RequestContext ctx) throws HttpException
	{
		if (!ctx.hasPostParam("usr") || !ctx.hasPostParam("pw"))
			throw new Http400BadRequestException();
		
		String token = znews.sessionManager.authenticate(ctx.getStringPostParam("usr"), ctx.getStringPostParam("pw"));
		
		if (token != null)
        {
            RequestResponse resp = new RequestResponse(token.getBytes(StandardCharsets.UTF_8));
            DefaultCookie authCookie = new DefaultCookie("znews_auth", token);
            authCookie.setMaxAge(Cookie.UNDEFINED_MAX_AGE);
            resp.addCookie(authCookie);
            return resp;
        }
		
		// Invalid credentials
		throw new Http403ForbiddenException("Invalid Credentials");
		
	}
	
}
