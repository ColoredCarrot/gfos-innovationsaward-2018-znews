package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import de.znews.server.auth.Admin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Newsletter implements Serializable, JsonSerializable
{
    
    private static final long serialVersionUID = -9142303673191329331L;
    
    @Setter(AccessLevel.NONE)
    private String  id;
    private String  title;
    private String  text;
    private boolean published;
    @Nullable
    private Date    datePublished;
    private UUID    publisher;
    
    public Newsletter(String title, String text, UUID publisher)
    {
        this.title = title;
        this.text = text;
        this.publisher = publisher;
        this.id = UUID.randomUUID().toString();
    }
    
    public String getPublisherName(ZNews znewsInstance)
    {
        return publisher == null
               ? "anonymous" :
               znewsInstance.authenticator.getAdmin(publisher)
                                          .map(Admin::getName)
                                          .orElse(publisher.toString());
    }
    
}
