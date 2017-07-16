package de.znews.server.resources.admin;

import de.znews.server.ZNews;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.RequestResponse;
import de.znews.server.resources.Resource;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.HttpResponseStatus;

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
			return new RequestResponse(token.getBytes(StandardCharsets.UTF_8));
		
		// Invalid credentials
		throw new HttpException(HttpResponseStatus.FORBIDDEN, "Invalid Credentials");
		
	}
	
}
