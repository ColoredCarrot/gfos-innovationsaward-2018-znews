package de.znews.server.netty;

import de.znews.server.Log;
import de.znews.server.resources.RequestResponse;
import de.znews.server.uri.URIFragment;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.HttpHeaderNames;

import java.nio.charset.StandardCharsets;
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
		          ? Collections.unmodifiableSet(ServerCookieDecoder.LAX.decode(in.headers().get(HttpHeaderNames.COOKIE)))
		          : Collections.emptySet();
		
		out.add(new NettyRequest(URIFragment.fromURI(in.getUri()), in.content(), cookies));
		in.content().retain();  // Retain the content because we need it in the next handler
		//out.add(URIFragment.fromURI(in.getUri().substring(1)));
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
        Log.err("Unhandled exception while handling request from remote address " + ctx.channel().remoteAddress() + " (local address " + ctx.channel().localAddress() + ")", cause);
        new RequestResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, HttpResponseStatus.INTERNAL_SERVER_ERROR.toString().getBytes(StandardCharsets.UTF_8)).respond(ctx);
	}
	
}
