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
		return Boolean.parseBoolean(value);
	}
	
	public int getValueAsInt(int defaultValue)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	
	@Override
	public String toString()
	{
		return key + '=' + value;
	}
}
