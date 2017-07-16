package de.znews.server.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Authenticator implements Serializable
{
	
	// <username, email>
	private Map<String, String> adminData = new HashMap<>();
	
	public boolean isAdmin(String username, String password)
	{
		return password.equals(adminData.get(username));
	}
	
	public void addAdmin(String username, String password)
	{
		adminData.put(username, password);
	}
	
}
