package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;

public class ByNidResource extends JSONResource
{
    
    public ByNidResource(ZNews znews)
    {
        super(znews, "admin/api/by_nid");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        znews.sessionManager.requireHttpAuthentication(ctx);
        
        String nid = ctx.getStringParam("nid");
        
        if (nid == null || nid.isEmpty())
            throw new Http400BadRequestException("Missing parameter 'nid'");
        
        try
        {
            Newsletter n = znews.newsletterManager.getNewsletter(nid);
            return JsonObject.createBuilder()
                             .add("success", true)
                             .add("data", JsonObject.createBuilder()
                                                    .add("title", n.getTitle())
                                                    .add("the_delta", n.getContent())
                                                    .add("tags", n.getTags())
                                                    .add("published", n.isPublished())
                                                    .build())
                             .build();
        }
        catch (IllegalArgumentException e)
        {
            // Newsletter not found
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder()
                                                     .add("code", Common.RS_ERR_NID_INVALID)
                                                     .add("message", "Newsletter not found")
                                                     .build())
                             .build();
        }
        
    }
    
}
