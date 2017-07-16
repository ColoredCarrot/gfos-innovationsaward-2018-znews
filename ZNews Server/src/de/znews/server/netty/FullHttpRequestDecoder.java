package de.znews.server.netty;

import de.znews.server.uri.URIFragment;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.HttpHeaderNames;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FullHttpRequestDecoder extends MessageToMessageDecoder<FullHttpRequest>
{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest in, List<Object> out) throws Exception
	{
		
		Set<Cookie> cookies;
		cookies = in.headers().contains(HttpHeaderNames.COOKIE)
		          ? Collections.unmodifiableSet(ServerCookieDecoder.STRICT.decode(in.headers().get(HttpHeaderNames.COOKIE)))
		          : Collections.emptySet();
		
		out.add(new NettyRequest(URIFragment.fromURI(in.getUri()), in.content(), cookies));
		in.content().retain();  // Retain the content because we need it in the next handler
		//out.add(URIFragment.fromURI(in.getUri().substring(1)));
		
	}
	
}
