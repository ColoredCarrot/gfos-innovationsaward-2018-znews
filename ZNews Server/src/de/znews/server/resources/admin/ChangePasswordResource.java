package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.auth.Admin;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.sessions.Session;

public class ChangePasswordResource extends JSONResource
{
    
    public ChangePasswordResource(ZNews znews)
    {
        // must be on admin/ for cookies to be sent
        super(znews, "admin/api/change_password");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
    
        Session authSession = znews.sessionManager.requireHttpAuthentication(ctx);
    
        String oldpw = ctx.getStringParam("oldpw");
        String newpw = ctx.getStringParam("newpw");
        
        if (oldpw == null || newpw == null)
            throw new Http400BadRequestException();
    
        Admin admin = authSession.getOwnerObject();
    
        if (!admin.checkPassword(oldpw))
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder()
                                                     .add("code", Common.RS_ERR_INVALID_OLD_PW)
                                                     .add("message", "Old password is not correct")
                                                     .build())
                             .build();
        }
    
        boolean b = admin.changePasswordTo(newpw, oldpw);
        assert b;
    
        return JsonObject.createBuilder().add("success", true).build();
        
    }
    
}
