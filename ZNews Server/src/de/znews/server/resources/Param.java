package de.znews.server.resources;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Param
{
	
	private final String key;
	private final String value;
	
	public boolean getValueAsBoolean()
	{
		return value.equalsIgnoreCase("true");
	}
	
	public int getValueAsInt()
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	@Override
	public String toString()
	{
		return key + '=' + value;
	}
}
