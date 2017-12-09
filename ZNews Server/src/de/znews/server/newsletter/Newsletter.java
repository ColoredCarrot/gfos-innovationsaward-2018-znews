package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Newsletter implements Serializable, JsonSerializable
{
	
	private static final long serialVersionUID = -9142303673191329331L;
	
	private String id;
	private String title;
	private String text;
	
}
