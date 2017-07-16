package de.znews.server.netty;

import de.znews.server.ZNews;
import de.znews.server.resources.Param;
import de.znews.server.resources.Params;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.Resource;
import de.znews.server.resources.SubscribeResource;
import de.znews.server.static_web.StaticWeb;
import de.znews.server.uri.URIFragment;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A <code>SimpleChannelInboundHandler</code> that accepts <code>URIFragment</code>s.<br>
 * Responsible for calling {@link Resource}s and {@link StaticWeb}
 */
@Getter
public class ResourceProviderHandler extends SimpleChannelInboundHandler<URIFragment>
{
	
	private final List<Resource> resources = new ArrayList<>();
	private final StaticWeb staticWeb;
	
	public ResourceProviderHandler(ZNews znews)
	{
		// FINDME: Register resources here
		resources.addAll(Arrays.asList(new SubscribeResource(znews)));
		staticWeb = new StaticWeb(new File("static_web"), znews.config.getStaticWebConfig());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, URIFragment firstFragment) throws Exception
	{
		
		System.out.println("Access " + firstFragment);
		
		for (Resource resource : resources)
			if (resource.appliesTo(firstFragment))
			{
				// We found a matching resource
				
				List<Param> params = new ArrayList<>();
				resource.getParams().forEachIndexed((i, f) ->
				{
					if (f.isParam())
						params.add(new Param(f.getAsParam(), firstFragment.get(i).getContent()));
				});
				
				resource.handleRequest(new RequestContext(ctx,
						new Params(params.toArray(new Param[params.size()])),
						new Params(firstFragment.getQuery().getParams()).withURLDecodedValues()))
				        .respond(ctx);
				
				return;
				
			}
		
		staticWeb.getResponse(firstFragment.toString()).respond(ctx);
	}
	
}
