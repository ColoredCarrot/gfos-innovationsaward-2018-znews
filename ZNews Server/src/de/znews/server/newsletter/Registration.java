package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Registration implements Serializable, JsonSerializable
{
    
    private static final long serialVersionUID = -3977523515006038071L;
    
    protected static Registration newStandardRegistration(ZNews znews, String email)
    {
        Registration reg = new Registration();
        reg.email = email;
        reg.subscribedTags.addAll(znews.tagsList.getTags());
        return reg;
    }
    
    private String email;
    private Set<String> subscribedTags = new HashSet<>();
    
    private Registration()
    {
    }
    
}
