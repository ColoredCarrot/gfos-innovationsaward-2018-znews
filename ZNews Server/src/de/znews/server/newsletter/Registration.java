package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Getter
public class Registration implements Serializable, JsonSerializable
{
    
    private static final long serialVersionUID = -3977523515006038071L;
    
    protected static Registration newStandardRegistration(ZNews znews, String email)
    {
        Registration reg = new Registration();
        reg.email = email;
        return reg;
    }
    
    @Setter
    private String email;
    private Set<String> subscribedTags;
    
    private Registration()
    {
    }
    
    public void setSubscribedTags(Set<String> subscribedTags)
    {
        this.subscribedTags = subscribedTags;
    }
    
    public Set<String> getAllSubscribedTags(ZNews znews)
    {
        return Collections.unmodifiableSet(subscribedTags != null ? subscribedTags : znews.tagsList.getTags());
    }
    
    public boolean isSubscribedToTag(String tag)
    {
        return subscribedTags == null || subscribedTags.contains(tag);
    }
    
}
