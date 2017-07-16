package de.znews.server.netty;

import de.znews.server.ZNews;
import de.znews.server.resources.Param;
import de.znews.server.resources.Params;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.Resource;
import de.znews.server.resources.SubscribeResource;
import de.znews.server.resources.admin.GetTokenResource;
import de.znews.server.resources.admin.PublishNewsletterResource;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.static_web.StaticWeb;
import de.znews.server.uri.URIQuery;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A <code>SimpleChannelInboundHandler</code> that accepts <code>URIFragment</code>s.<br>
 * Responsible for calling {@link Resource}s and {@link StaticWeb}
 */
@Getter
public class ResourceProviderHandler extends SimpleChannelInboundHandler<NettyRequest>
{
	
	private final List<Resource> resources = new ArrayList<>();
	private final StaticWeb staticWeb;
	
	public ResourceProviderHandler(ZNews znews, StaticWeb staticWeb)
	{
		// FINDME: Register resources here
		resources.addAll(Arrays.asList(new SubscribeResource(znews), new GetTokenResource(znews), new PublishNewsletterResource(znews)));
		this.staticWeb = staticWeb;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NettyRequest request) throws Exception
	{
		
		try
		{
			
			System.out.println("Access " + request.getUri());
			
			for (Resource resource : resources)
				if (resource.appliesTo(request.getUri()))
				{
					// We found a matching resource
					
					// Convert post-data ByteBuf to Params
					// We utilize URIQuery because the post data is also
					//  encoded x-www-form-urlencoded
					Params postParams = URIQuery.fromString(request.getPost().toString(StandardCharsets.UTF_8)).toParams().withURLDecodedValues();
					
					// URL params (/api/{version})
					List<Param> params = new ArrayList<>();
					resource.getParams().forEachIndexed((i, f) ->
					{
						if (f.isParam())
							params.add(new Param(f.getAsParam(), request.getUri().get(i).getContent()));
					});
					
					try
					{
						resource.handleRequest(new RequestContext(ctx,
								new Params(params.toArray(new Param[params.size()])),           // URL params
								request.getUri().getQuery().toParams().withURLDecodedValues(),  // Query params
								postParams,                                                     // Post/Put params
								Params.fromCookies(request.getCookies())))                      // Cookie params
						        .respond(ctx);
					}
					catch (HttpException e)
					{
						e.toResponse().respond(ctx);
					}
					
					return;
					
				}
			
			staticWeb.getResponse(request.getUri().toString()).respond(ctx);
			
		}
		finally
		{
			// We retained the post data in the previous handler
			request.releasePost();
		}
		
	}
	
}
