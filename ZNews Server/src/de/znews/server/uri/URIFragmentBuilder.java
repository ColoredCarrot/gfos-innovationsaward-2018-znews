package de.znews.server.uri;

import java.util.ArrayList;
import java.util.List;

public class URIFragmentBuilder
{
	
	private List<URIFragment> fragments = new ArrayList<>();
	
	public URIFragmentBuilder add(URIFragment fragment)
	{
		fragments.add(fragment);
		return this;
	}
	
	public URIFragmentBuilder add(String... fragments)
	{
		for (String fragment : fragments)
			add(URIFragment.fromFragment(fragment));
		return this;
	}
	
	public URIFragment build()
	{
		if (fragments.isEmpty())
			return null;
		
		for (int i = 0; i < fragments.size() - 1; i++)
			fragments.get(i).next = fragments.get(i + 1);
		
		return fragments.get(0);
		
	}
	
}
