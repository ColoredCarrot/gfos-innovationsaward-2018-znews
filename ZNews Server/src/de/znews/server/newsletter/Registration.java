package de.znews.server.newsletter;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Registration implements Serializable
{
	
	private static final long serialVersionUID = -8221233446973804554L;
	
	protected static Registration newStandardRegistration(String email)
	{
		Registration reg = new Registration();
		reg.email = email;
		return reg;
	}
	
	private String email;
	
	private Registration()
	{
	}
	
}
