package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.sessions.Session;

public class LogoutResource extends JSONResource
{
    
    public LogoutResource(ZNews znews)
    {
        super(znews, "admin/api/logout");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
    
        Session session = znews.sessionManager.requireHttpAuthentication(ctx);
    
        znews.sessionManager.invalidateSession(session);
    
        return JsonObject.createBuilder()
                         .add("success", true)
                         .build();
        
    }
    
}
