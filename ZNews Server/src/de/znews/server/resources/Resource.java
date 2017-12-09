package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public abstract class Resource
{
	
	protected final ZNews znews;
	
	@Getter
	private URIFragment params;
	private List<URIFragment> alternativeParams;
	
	public Resource(ZNews znews, URIFragment params)
	{
		this.znews = znews;
		this.params = params;
        this.alternativeParams = null;
	}
    
    public Resource(ZNews znews, URIFragment params, URIFragment... alternativeParams)
    {
        this.znews = znews;
        this.params = params;
        this.alternativeParams = Arrays.asList(alternativeParams);
    }
    
    public abstract RequestResponse handleRequest(RequestContext ctx) throws HttpException;
	
	public boolean appliesTo(URIFragment firstFragment)
	{
		return firstFragment.compare(params) || alternativeParams != null && alternativeParams.stream().anyMatch(firstFragment::compare);
	}
	
}
