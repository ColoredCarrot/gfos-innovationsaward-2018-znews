package de.znews.server.resources.admin;

import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.RequestResponse;
import de.znews.server.resources.Resource;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;

import java.nio.charset.StandardCharsets;

public class PublishNewsletterResource extends Resource
{
	
	public PublishNewsletterResource(ZNews znews)
	{
		super(znews, URIFragment.fromURI("api/v1/admin/publish_newsletter"));
	}
	
	@Override
	public RequestResponse handleRequest(RequestContext ctx) throws HttpException
	{
		// Make sure the user is authorized
		if (!ctx.hasCookieParam("stoken") || !znews.sessionManager.isAuthenticated(ctx.getStringCookieParam("stoken")))
			throw new Http403ForbiddenException();
		// Make sure the required data is present
		if (!ctx.hasParam("n_title") || !ctx.hasParam("n_text"))
			throw new Http400BadRequestException();
		
		Newsletter newsletter = new Newsletter(ctx.getStringParam("n_title"), ctx.getStringParam("n_text"));
		
		znews.newsletterManager.addNewsletter(newsletter);
		
		return new RequestResponse("Success".getBytes(StandardCharsets.UTF_8));
		
	}
	
}
