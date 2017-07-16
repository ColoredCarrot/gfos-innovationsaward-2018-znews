package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;

public class SubscribeResource extends Resource
{
	
	public SubscribeResource(ZNews znews)
	{
		//super(znews, new URIFragmentBuilder().add("api", "v1", "subscribe").build());
		super(znews, URIFragment.fromURI("api/v1/subscribe"));
	}
	
	@Override
	public RequestResponse handleRequest(RequestContext ctx)
	{
		
		if (!ctx.hasQueryParam("email"))
			return new RequestResponse(HttpResponseStatus.BAD_REQUEST, "Missing email parameter".getBytes(StandardCharsets.UTF_8));
		
		znews.registrationList.registerNewEmail(ctx.getStringParam("email"));
		
		return new RequestResponse("Success".getBytes(StandardCharsets.UTF_8));
		
	}
	
}
