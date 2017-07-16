package de.znews.server.newsletter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RegistrationList implements Serializable
{
	
	private static final long serialVersionUID = 1245091416250410698L;
	
	private final Map<String, Registration> registeredEmails = new HashMap<>();
	
	public Registration registerNewEmail(String email)
	{
		Registration reg = Registration.newStandardRegistration(email);
		registeredEmails.put(email, reg);
		return reg;
	}
	
	public Registration getRegistration(String email)
	{
		return registeredEmails.get(email);
	}
	
}
