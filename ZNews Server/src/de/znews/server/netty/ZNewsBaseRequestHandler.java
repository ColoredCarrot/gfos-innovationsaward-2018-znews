package de.znews.server.netty;

import de.znews.server.uri.URIFragment;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class ZNewsBaseRequestHandler extends MessageToMessageDecoder<FullHttpRequest>
{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest in, List<Object> out) throws Exception
	{
		out.add(new URIFragment(in.getUri().substring(1)));
	}
	
}
