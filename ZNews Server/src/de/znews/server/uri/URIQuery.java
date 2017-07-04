package de.znews.server.uri;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class URIQuery
{
	
	@Getter
	private Map<String, String> params = new HashMap<>();
	
	public URIQuery(String s)
	{
		
		if (s.startsWith("?"))
			s = s.substring(1);
		
		StringBuilder sb = new StringBuilder();
		
		char[] c = s.toCharArray();
		int    i = 0;
		
		while (i < c.length)
		{
			
			StringBuilder nameBuilder = new StringBuilder();
			
			char nameChar = 0;
			while (i < c.length && (nameChar = c[i++]) != '=' && nameChar != '&')
				nameBuilder.append(nameChar);
			
			if (nameChar == '=')
			{
				StringBuilder valueBuilder = new StringBuilder();
				char          valueChar;
				while (i < c.length && (valueChar = c[i++]) != '&')
					valueBuilder.append(valueChar);
				params.put(nameBuilder.toString(), valueBuilder.toString());
			}
			else
				params.put(nameBuilder.toString(), "");
			
		}
		
	}
	
	
	@Override
	public String toString()
	{
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('?');
		
		params.forEach((key, value) ->
		{
			sb.append(key);
			if (!value.trim().isEmpty())
			{
				sb.append('=');
				sb.append(value);
			}
			sb.append('&');
		});
		
		if (sb.length() != 1)
			sb.setLength(sb.length() - 1);
		
		return sb.toString();
		
	}
	
}
