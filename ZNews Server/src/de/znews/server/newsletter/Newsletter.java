package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import de.znews.server.auth.Admin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Newsletter implements Serializable, JsonSerializable
{
    
    private static final long serialVersionUID = 508675215015238869L;
    
    @Setter(AccessLevel.NONE)
    private String       id;
    private String       title;
    private JsonArray    content;
    @Getter(AccessLevel.NONE)
    private List<String> tags;
    private boolean      published;
    @Nullable
    private Date         datePublished;
    private UUID         publisher;
    private long         views;
    
    public Newsletter(String title, JsonArray content, UUID publisher)
    {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.id = UUID.randomUUID().toString();
    }
    
    public String getPublisherName(ZNews znewsInstance)
    {
        return publisher == null
               ? "anonymous" :  // Legacy
               znewsInstance.authenticator.getAdmin(publisher)
                                          .map(Admin::getName)
                                          .orElse(publisher.toString());
    }
    
    public String[] getTags()
    {
        return tags != null ? tags.toArray(new String[0]) : new String[0];
    }
    
    public synchronized long incrementViews()
    {
        return ++views;
    }
    
}
