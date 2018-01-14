package de.znews.server.resources;

import de.znews.server.Log;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.validator.routines.EmailValidator;

public class CancelSubscriptionResource extends Resource
{
    
    private static final Object lock = new Object();
    
    public CancelSubscriptionResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("cancel_subscription"));
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
            if (!znews.registrationList.removeRegistration(email))
                throw new Http400BadRequestException("Email not registered");
        }
    
        try
        {
            return znews.staticWeb.getResponse("/cancel_subscription");
        }
        catch (InterruptedException e)
        {
            Log.warn("Could not cancel subscription of " + email + "!", e);
            throw new HttpException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Could not cancel subscription");
        }
    
    }
    
}
