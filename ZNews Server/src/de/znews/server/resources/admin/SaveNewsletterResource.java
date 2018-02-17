package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.parsing.JsonException;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.sessions.Session;

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
        
        Session authSession = znews.sessionManager.requireHttpAuthentication(ctx);
        
        String newTitle   = ctx.getStringParam("title");
        String newText    = ctx.getStringParam("text");
        String newTagsStr = ctx.getStringParam("tags");
        
        // Check if both new title and text are supplied
        // TODO: If only one isn't, proceed anyway, not overwriting the corresponding old value
        if (newTitle == null && newText == null)
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder().add("code", Common.RS_ERR_SAVE_MISSING_TITLE).add("message", "Missing title and/or text parameter").build())
                             .build();
        }
        
        // Parse newTagsStr
        String[] newTags;
        if (newTagsStr == null)
            newTags = null;
        else try
        {
            newTags = Json.deserializeFromString(newTagsStr, String[].class);
        }
        catch (JsonException e)
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder().add("code", Common.RS_ERR_SAVE_INVALID_TAGS).add("message", "Failed to parse tags parameter: " + e.getMessage()).build())
                             .build();
        }
        
        String newsletterId = ctx.getStringParam("nid");
        
        if (newsletterId == null)
        {
            // Create new newsletter
            Newsletter n = new Newsletter(newTitle, newText, authSession.getOwner());
            n.setTags(newTags);
            znews.newsletterManager.addNewsletter(n);
            newsletterId = n.getId();
        }
        else
        {
            try
            {
                Newsletter n = znews.newsletterManager.getNewsletter(newsletterId);
                if (newTitle != null)
                    n.setTitle(newTitle);
                if (newText != null)
                    n.setText(newText);
                if (newTags != null)
                    n.setTags(newTags);
            }
            catch (IllegalArgumentException e)
            {
                return JsonObject.createBuilder()
                                 .add("success", false)
                                 .add("error", JsonObject.createBuilder().add("code", Common.RS_ERR_SAVE_INVALID_NID).add("message", "Invalid Newsletter ID").build())
                                 .build();
            }
        }
        
        JsonObject.Builder data = JsonObject.createBuilder();
        
        data.add("nid", newsletterId);
        data.add("title", newTitle);
        // don't add text; would be too much
        // TODO: instead, compute hash and send that
        
        return JsonObject.createBuilder().add("success", true).add("data", data.build()).build();
        
    }
    
}
