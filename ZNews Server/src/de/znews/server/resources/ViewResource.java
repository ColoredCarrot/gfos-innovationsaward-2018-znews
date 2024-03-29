package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;

import java.util.Objects;

public class ViewResource extends JSONResource
{
    
    public ViewResource(ZNews znews)
    {
        super(znews, "admin/api/view");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        String nid = ctx.getStringParam("nid");
        
        if (nid == null || nid.isEmpty())
            throw new Http400BadRequestException("Missing parameter 'nid'");
        
        try
        {
            Newsletter n = znews.newsletterManager.getNewsletter(nid);
            
            // If not published, treat as if the newsletter didn't exist IF user is not logged in
            if (!n.isPublished() && !znews.sessionManager.authenticate(ctx.getStringCookieParam("znews_auth")).isPresent())
                throw new Http400BadRequestException("Invalid Newsletter ID");
    
            n.incrementViews();
            
            JsonObject.Builder dataJson = JsonObject.createBuilder();
            dataJson.add("title", n.getTitle())
                    .add("the_delta", n.getContent());
            if (n.isPublished())
                dataJson.add("datePublished", Objects.requireNonNull(n.getDatePublished()).getTime())
                        .add("publisher", n.getPublisherName(znews));
            else
                dataJson.add("published", false);
    
            return JsonObject.createBuilder()
                             .add("success", true)
                             .add("data", dataJson.build())
                             .build();
        }
        catch (IllegalArgumentException e)
        {
            // Newsletter not found
            throw new Http400BadRequestException("Invalid Newsletter ID");
        }
        
    }
    
}
