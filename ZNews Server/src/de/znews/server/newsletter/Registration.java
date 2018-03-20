package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Getter
public class Registration implements Serializable, JsonSerializable
{
    
    private static final long serialVersionUID = 7257088997484675470L;
    
    protected static Registration newStandardRegistration(ZNews znews, String email)
    {
        Registration reg = new Registration();
        reg.email = email;
        reg.dateRegistered = new Date();
        return reg;
    }
    
    @Setter
    private String email;
    private Set<String> subscribedTags;
    private Date dateRegistered;
    
    private Registration()
    {
    }
    
    public Registration(String email, Set<String> subscribedTags, Date dateRegistered)
    {
        this.email = email;
        this.subscribedTags = subscribedTags;
        this.dateRegistered = dateRegistered;
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
