package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import org.apache.commons.validator.routines.EmailValidator;

public class SubscribeResource extends JSONResource
{
    
    public SubscribeResource(ZNews znews)
    {
        //super(znews, new URIFragmentBuilder().add("api", "v1", "subscribe").build());
        super(znews, "api/v1/subscribe/{email}");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        String email = ctx.getStringParam("email");
    
        if (!EmailValidator.getInstance().isValid(email))
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder()
                                                     .add("code", Common.RS_ERR_REG_INVALID_EMAIL)
                                                     .add("message", "Invalid email address").build())
                             .build();
        }
    
        if (znews.registrationList.isRegistered(email))
        {
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder()
                                                     .add("code", Common.RS_ERR_REG_ALREADY_REGISTERED)
                                                     .add("message", "Email already registered").build())
                             .build();
        }
        
        znews.registrationList.registerNewEmail(email);
        
        return JsonObject.createBuilder().add("success", true).build();
        
    }
    
}
