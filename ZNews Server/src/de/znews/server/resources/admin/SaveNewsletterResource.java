package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.HttpException;

public class SaveNewsletterResource extends JSONResource
{
    
    public SaveNewsletterResource(ZNews znews)
    {
        // must be on admin/ for cookies to be sent
        super(znews, "admin/api/v1/newsletter/save");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        znews.authenticator.requireAuthentication(ctx);
    
        String newsletterId = ctx.getStringParam("nid");
    
        if (newsletterId == null)
        {
            // Create new newsletter
            
        }
        
        return JsonObject.createBuilder().add("success", true).build();
        
    }
    
}
