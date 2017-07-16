package de.znews.server.sessions;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;

@Getter(AccessLevel.PROTECTED)
public class Session
{
	
	static Session newSession(String username, String password)
	{
		return new Session(UUID.randomUUID().toString(), username, password);
	}
	
	private final String token;
	private final String username;
	private final String password;
	
	private Session(String token, String username, String password)
	{
		this.token = token;
		this.username = username;
		this.password = password;
	}
	
}
