package de.znews.server.uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class URIFragmentBuilder
{
	
	private List<URIFragment> fragments = new ArrayList<>();
	private Map<String, String> query;
	
	public URIFragmentBuilder add(URIFragment fragment)
	{
		fragments.add(fragment);
		return this;
	}
	
	public URIFragmentBuilder add(String... fragments)
	{
		for (String fragment : fragments)
			add(URIFragment.withContent(fragment));
		return this;
	}
	
	public URIFragmentBuilder addQueryParam(String key)
	{
		return addQuery(key, '{' + key + '}');
	}
	
	public URIFragmentBuilder addQuery(String key)
	{
		return addQuery(key, "");
	}
	
	public URIFragmentBuilder addQuery(String key, String value)
	{
		query.put(key, value);
		return this;
	}
	
	public URIFragment build()
	{
		if (fragments.isEmpty())
			return null;
		
		for (int i = 0; i < fragments.size() - 1; i++)
			fragments.get(i).next = fragments.get(i + 1);
		
		if (query != null)
			fragments.get(fragments.size() - 1).query = new URIQuery(query);
		
		return fragments.get(0);
		
	}
	
}
