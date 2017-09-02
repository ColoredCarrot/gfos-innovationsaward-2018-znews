package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Newsletter implements Serializable, JsonSerializable
{
	
	private static final long serialVersionUID = -9142303673191329331L;
	
	private String title;
	private String text;
	
	public Newsletter()
	{
	}
	
}
