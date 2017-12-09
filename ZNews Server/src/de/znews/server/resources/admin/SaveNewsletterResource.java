package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.HttpException;

public class SaveNewsletterResource extends JSONResource
{
    
    public SaveNewsletterResource(ZNews znews)
    {
        // must be on admin/ for cookies to be sent
        super(znews, "admin/api/newsletter/save");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        znews.authenticator.requireAuthentication(ctx);
        
        String newTitle = ctx.getStringParam("title");
        String newText  = ctx.getStringParam("text");
        
        // Check if both new title and text are supplied
        // TODO: If only one isn't, proceed anyway, not overwriting the corresponding old value
        if (newTitle == null || newText == null)
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", newTitle == null
                                           ? JsonObject.createBuilder().add("code", Common.RS_ERR_SAVE_MISSING_TITLE).add("message", "Missing title parameter").build()
                                           : JsonObject.createBuilder().add("code", Common.RS_ERR_SAVE_MISSING_TEXT ).add("message", "Missing text parameter" ).build())
                             .build();
        }
        
        String newsletterId = ctx.getStringParam("nid");
        
        if (newsletterId == null)
        {
            // Create new newsletter
            Newsletter n = new Newsletter(newTitle, newText);
            znews.newsletterManager.addNewsletter(n);
        }
        else
        {
        
        }
        
        JsonObject.Builder data = JsonObject.createBuilder();
        
        data.add("nid", newsletterId);
        data.add("title", newTitle);
        // don't add text; would be too much
        
        return JsonObject.createBuilder().add("success", true).add("data", data.build()).build();
        
    }
    
}
