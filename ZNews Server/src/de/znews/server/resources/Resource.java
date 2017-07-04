package de.znews.server.resources;

import de.znews.server.uri.URIFragment;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Resource
{
	
	private         URIFragment params;
	@Getter private String[]    paramNames;
	
	public Resource(URIFragment params)
	{
		this.params = params;
		
		List<String> paramNames = new ArrayList<>();
		params.forEach(f ->
		{
			if (f.isParam())
				paramNames.add(f.getAsParam());
			else
				paramNames.add(null);
		});
		this.paramNames = paramNames.toArray(new String[0]);
		
	}
	
	public abstract void handleRequest(ChannelHandlerContext ctx, Param... params);
	
	public boolean appliesTo(URIFragment firstFragment)
	{
		return firstFragment.compare(params);
	}
	
}
