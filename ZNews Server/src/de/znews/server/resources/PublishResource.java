package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;

public class PublishResource extends JSONResource
{
    
    public PublishResource(ZNews znews)
    {
        super(znews, "admin/api/publish");
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
            synchronized (znews.newsletterManager)
            {
                znews.newsletterManager.doPublishNewsletter(nid);
                
                return JsonObject.createBuilder()
                                 .add("success", true)
                                 .build();
            }
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
