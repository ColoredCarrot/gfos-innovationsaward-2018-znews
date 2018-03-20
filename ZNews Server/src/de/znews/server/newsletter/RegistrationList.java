package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RegistrationList implements Serializable, JsonSerializable, Iterable<Registration>
{
	
	private static final long serialVersionUID = 1245091416250410698L;
    
    private final Map<String, Registration> registeredEmails = new HashMap<>();
    
    public Registration registerNewEmail(ZNews znews, String email)
	{
		Registration reg = Registration.newStandardRegistration(znews, email);
		registeredEmails.put(email, reg);
		return reg;
	}
    
    public boolean removeRegistration(String email)
    {
        return registeredEmails.remove(email) != null;
    }
	
	public Registration getRegistration(String email)
	{
		return registeredEmails.get(email);
	}
    
    public Registration changeRegistrationEmail(String oldEmail, String newEmail)
    {
        Registration reg = getRegistration(oldEmail);
        if (reg == null)
            return null;
        registeredEmails.put(newEmail, reg);
        registeredEmails.remove(oldEmail);
        reg.setEmail(newEmail);
        return reg;
    }
    
    public boolean isRegistered(String email)
    {
        return registeredEmails.containsKey(email);
    }
    
    public Stream<Registration> getAllRegistrations()
    {
        return registeredEmails.values().stream();
    }
    
    public void addRegistration(Registration reg)
    {
        registeredEmails.put(reg.getEmail(), reg);
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
    
    @NotNull
    @Override
    public Iterator<Registration> iterator()
    {
        return registeredEmails.values().iterator();
    }
    
    @Override
    public void forEach(Consumer<? super Registration> action)
    {
        registeredEmails.values().forEach(action);
    }
    
    @Override
    public Spliterator<Registration> spliterator()
    {
        return registeredEmails.values().spliterator();
    }
    
    public int getNumRegistrations()
    {
        return registeredEmails.size();
    }
    
    // TODO: Add proper synchronization
    public RegistrationList snapshot()
    {
        RegistrationList snapshot = new RegistrationList();
        snapshot.registeredEmails.putAll(registeredEmails);
        return snapshot;
    }
    
}
