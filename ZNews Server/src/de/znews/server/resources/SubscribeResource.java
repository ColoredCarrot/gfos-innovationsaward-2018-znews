package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.Common;
import de.znews.server.ZNews;
import de.znews.server.emai_reg.DoubleOptInEmail;
import de.znews.server.resources.exception.HttpException;
import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.MessagingException;

public class SubscribeResource extends JSONResource
{
    
    public SubscribeResource(ZNews znews)
    {
        //super(znews, new URIFragmentBuilder().add("api", "v1", "subscribe").build());
        //super(znews, "api/subscribe/{email}");
        super(znews, "api/subscribe?email={email}&t={DISABLE_CACHING_THROWAWAY_VAR}");  // TODO: The throwaway var should not have to be declared...
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
        
        //znews.registrationList.registerNewEmail(email);
        DoubleOptInEmail doubleOptInEmail = new DoubleOptInEmail(znews);
        doubleOptInEmail.setRegisteredEmail(email);
        
        try
        {
            doubleOptInEmail.send(email);
        }
        catch (MessagingException e)
        {
            // Failed to send email
            e.printStackTrace();
            return JsonObject.createBuilder()
                             .add("success", false)
                             .add("error", JsonObject.createBuilder()
                                                     .add("code", "FAILED_TO_SEND_EMAIL" /*FIXME: Create Common.-entry*/)
                                                     .add("message", "Failed to send double opt-in email")
                                                     .build())
                             .build();
        }
        
        return JsonObject.createBuilder().add("success", true).build();
        
    }
    
}
