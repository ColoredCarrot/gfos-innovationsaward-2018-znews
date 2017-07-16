package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import lombok.Getter;

public abstract class Resource
{
	
	protected final ZNews znews;
	
	@Getter
	private URIFragment params;
	
	public Resource(ZNews znews, URIFragment params)
	{
		this.znews = znews;
		this.params = params;
	}
	
	public abstract RequestResponse handleRequest(RequestContext ctx) throws HttpException;
	
	public boolean appliesTo(URIFragment firstFragment)
	{
		return firstFragment.compare(params);
	}
	
}
