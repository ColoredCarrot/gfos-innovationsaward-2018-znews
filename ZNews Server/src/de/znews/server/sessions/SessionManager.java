package de.znews.server.sessions;

import de.znews.server.auth.Authenticator;

import java.util.HashMap;
import java.util.Map;

public class SessionManager
{
	
	private final Authenticator authenticator;
	private final Map<String, Session> sessionMap = new HashMap<>();
	
	public SessionManager(Authenticator authenticator)
	{
		this.authenticator = authenticator;
	}
	
	public String authenticate(String username, String password)
	{
		// Invalid credentials?
		if (!authenticator.isAdmin(username, password))
			return null;
        
        return sessionMap.computeIfAbsent(username, usr -> Session.newSession(username, password)).getToken();
		
	}
	
	public boolean isAuthenticated(String token)
	{
		//return sessionMap.containsKey(token);
        return sessionMap.values().stream().anyMatch(s -> s.getToken().equals(token));
	}
	
}
