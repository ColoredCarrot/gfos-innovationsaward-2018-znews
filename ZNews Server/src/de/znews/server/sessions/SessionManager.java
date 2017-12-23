package de.znews.server.sessions;

import de.znews.server.auth.Authenticator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager
{
	
	private final Authenticator authenticator;
	private final Map<String, Session> sessionMap = new HashMap<>();
	
	public SessionManager(Authenticator authenticator)
	{
		this.authenticator = authenticator;
	}
	
	public Optional<Session> authenticate(String email, String password)
	{
		// Invalid credentials?
        
        return authenticator.authenticate(email, password)
                            .map(admin -> sessionMap.computeIfAbsent(email, usr -> Session.newSession(admin)));
		
        /*if (!authenticator.authenticate(email, password).isPresent())
            return null;
        
        return sessionMap.computeIfAbsent(email, usr -> Session.newSession(email, password)).getToken();*/
		
	}
	
	public Optional<Session> isAuthenticated(String token)
	{
		///return sessionMap.containsKey(token);
        return sessionMap.values().stream()
                         .filter(s -> s.getToken().equals(token))
                         .findFirst();
	}
	
}
