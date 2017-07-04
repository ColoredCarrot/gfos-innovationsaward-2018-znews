package de.znews.server.netty;

import de.znews.server.Main;
import de.znews.server.resources.APIResource;
import de.znews.server.resources.Param;
import de.znews.server.staticweb.StaticWeb;
import de.znews.server.uri.URIFragment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ResourceProviderHandler extends SimpleChannelInboundHandler<URIFragment>
{
	
	private APIResource api = new APIResource();
	private byte[] favicon;
	private StaticWeb staticWeb = new StaticWeb(new File("static_web"));
	
	@SneakyThrows
	public ResourceProviderHandler()
	{
		
		try (InputStream in = Main.class.getResourceAsStream("/resources/favicon.ico");
		     ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			int read;
			while ((read = in.read()) != -1)
				out.write(read);
			favicon = out.toByteArray();
		}
		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, URIFragment firstFragment) throws Exception
	{
		
		// TODO why dis no work?!??! maybe dimensions not correct?
		if (firstFragment.toString().equals("favicon.ico"))
		{
			ByteBuf faviconByteBuf = ctx.alloc().buffer(favicon.length);
			ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, faviconByteBuf));
			ctx.close();
			return;
		}
		
		System.out.println("Access " + firstFragment);
		
		if (api.appliesTo(firstFragment))
		{
			
			List<Param> params = new ArrayList<>();
			
			for (int i = 0; i < api.getParamNames().length; i++)
				if (api.getParamNames()[i] != null)
					params.add(new Param(api.getParamNames()[i], firstFragment.get(i).get()));
			
			api.handleRequest(ctx, params.toArray(new Param[0]));
			
		}
		else
		{
			staticWeb.send404NotFound(ctx);
		}
		
		ReferenceCountUtil.release(firstFragment);
		ctx.close();
		
	}
	
}
