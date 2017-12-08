package de.znews.server.auth;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Authenticator implements Serializable, JsonSerializable
{
	
    public transient ZNews znews;
    
	// <username, email>
	private Map<String, String> adminData = new HashMap<>();
    
    public Authenticator(ZNews znews)
    {
        this.znews = znews;
    }
    
    public boolean isAdmin(String username, String password)
	{
		return password.equals(adminData.get(username));
	}
	
	public void addAdmin(String username, String password)
	{
		adminData.put(username, password);
	}
    
    public void requireAuthentication(RequestContext ctx) throws HttpException
    {
        if (!ctx.hasCookieParam("znews_auth"))
            throw new Http403ForbiddenException("Authentication cookie missing");
        String auth = ctx.getStringCookieParam("znews_auth");
        if (!znews.sessionManager.isAuthenticated(auth))
            throw new Http403ForbiddenException("Authentication cookie invalid");
    }
    
}
