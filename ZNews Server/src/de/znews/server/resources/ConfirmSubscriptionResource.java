package de.znews.server.resources;

import de.znews.server.Log;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.static_web.StaticWeb;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.validator.routines.EmailValidator;

public class ConfirmSubscriptionResource extends Resource
{
    
    private static final Object lock = new Object();
    
    public ConfirmSubscriptionResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("confirm_subscription"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        
        if (!ctx.hasParam("email"))
            throw new Http400BadRequestException("Missing email parameter");
        
        String email = ctx.getStringParam("email");
        
        if (!EmailValidator.getInstance().isValid(email))
            throw new Http400BadRequestException("Invalid email");
    
        synchronized (lock)
        {
            if (znews.registrationList.isRegistered(email))
                throw new Http400BadRequestException("Email already registered");
            znews.registrationList.registerNewEmail(email);
        }
    
        StaticWeb staticWeb = null;
        try
        {
            return znews.staticWeb.getResponse("/confirm_subscription");
        }
        catch (InterruptedException e)
        {
            Log.warn("Could not confirm subscription of " + email + "!", e);
            throw new HttpException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Could not confirm subscription");
        }
    
    }
    
}
