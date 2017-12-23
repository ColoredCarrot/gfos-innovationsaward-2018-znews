package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteResource extends JSONResource
{
    
    private static final List<String> TRUE_STRINGS = Collections.unmodifiableList(Arrays.asList("true", "1", "ok", "yes"));
    
    public DeleteResource(ZNews znews)
    {
        super(znews, "admin/api/delete");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        znews.sessionManager.requireHttpAuthentication(ctx);
        
        String  nid   = ctx.getStringParam("nid");
        Integer hash  = ctx.hasParam("hash") ? ctx.getIntParam("hash", Integer.MIN_VALUE) : null;
        boolean force = ctx.hasParam("force") && TRUE_STRINGS.contains(ctx.getStringParam("force"));
        
        if (nid == null || nid.isEmpty())
            throw new Http400BadRequestException("Missing parameter 'nid'");
        if (hash != null && force)
            throw new Http400BadRequestException("Cannot specify both 'hash' and 'force'");
        
        try
        {
            synchronized (znews.newsletterManager)
            {
    
                Newsletter n = znews.newsletterManager.getNewsletter(nid);
    
                if (force || hash == null || hash == (n.getTitle() + n.getText()).hashCode())
                {
                    znews.newsletterManager.doDeleteNewsletter(nid);
    
                    return JsonObject.createBuilder()
                                     .add("success", true)
                                     .build();
                }
                else
                {
                    return JsonObject.createBuilder()
                                     .add("success", false)
                                     .add("error", JsonObject.createBuilder()
                                                             .add("code", Common.RS_ERR_NOT_DELETED)
                                                             .add("message", "Newsletter was not deleted")
                                                             .build())
                                     .build();
                }
                
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
