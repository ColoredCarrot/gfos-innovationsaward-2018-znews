package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RegistrationList implements Serializable, JsonSerializable
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
    
    @JsonDeserializer
    static RegistrationList deserializeJson(JsonObject json)
    {
        JsonObject       emailsObj = json.getObject("registeredEmails");
        RegistrationList result = new RegistrationList();
        for (String email : emailsObj.getKeys())
            result.registeredEmails.put(email, emailsObj.builder().get(email, Registration.class));//Json.deserialize(serializedRegistration, Registration.class));
        return result;
    }
    
}
