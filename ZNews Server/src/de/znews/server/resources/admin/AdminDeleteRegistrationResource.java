package de.znews.server.resources.admin;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.resources.JSONResource;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.Http404NotFoundException;
import de.znews.server.resources.exception.HttpException;

public class AdminDeleteRegistrationResource extends JSONResource
{

    public AdminDeleteRegistrationResource(ZNews znews)
    {
        super(znews, "admin/api/admin_delete_registration");
    }

    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        znews.sessionManager.requireHttpAuthentication(ctx);

        String email = ctx.getStringParam("email");

        if (email == null)
            throw new Http400BadRequestException("missing email parameter");

        if (!znews.registrationList.removeRegistration(email))
            throw new Http404NotFoundException("email not registered: " + email);

        return JsonObject.createBuilder()
                         .add("success", true)
                         .add("deletedEmail", email)
                         .build();

    }

}
