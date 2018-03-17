package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Registration;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.Http404NotFoundException;
import de.znews.server.resources.exception.HttpException;

public class AdminEditRegistrationResource extends JSONResource
{
    
    public AdminEditRegistrationResource(ZNews znews)
    {
        super(znews, "admin/api/admin_edit_registration");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        znews.sessionManager.requireHttpAuthentication(ctx);
    
        String oldEmail = ctx.getStringParam("oldemail");
        String newEmail = ctx.getStringParam("newemail");
        
        if (oldEmail == null || newEmail == null)
            throw new Http400BadRequestException("missing oldemail or newemail parameter");
    
        Registration reg = znews.registrationList.getRegistration(oldEmail);
    
        if (reg == null)
            throw new Http404NotFoundException("email not registered: " + oldEmail);
    
        reg.setEmail(newEmail);
    
        return JsonObject.createBuilder()
                         .add("success", true)
                         .add("oldemail", oldEmail)
                         .add("newemail", reg.getEmail())
                         .build();
        
    }
    
}
