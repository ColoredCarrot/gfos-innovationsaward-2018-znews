package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Newsletter implements Serializable, JsonSerializable
{
	
	private static final long serialVersionUID = -9142303673191329331L;
	
	@Setter(AccessLevel.NONE)
	private String id;
	private String title;
	private String text;
	private boolean published = false;
    
    public Newsletter(String title, String text)
    {
        this.title = title;
        this.text = text;
        this.id = UUID.randomUUID().toString();
    }
    
}
