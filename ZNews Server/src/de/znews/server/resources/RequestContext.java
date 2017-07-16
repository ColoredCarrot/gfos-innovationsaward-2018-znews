package de.znews.server.resources;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.Getter;

@Getter
public class RequestContext
{
	
	private final ChannelHandlerContext channelHandlerContext;
	
	private final Params urlParams;
	private final Params queryParams;
	private final Params postParams;
	private final Params cookieParams;
	private final Params params;
	
	public RequestContext(ChannelHandlerContext channelHandlerContext, Params urlParams, Params queryParams, Params postParams, Params cookieParams)
	{
		this.channelHandlerContext = channelHandlerContext;
		this.urlParams = urlParams;
		this.queryParams = queryParams;
		this.postParams = postParams;
		this.cookieParams = cookieParams;
		this.params = new Params(urlParams, queryParams, postParams, cookieParams);
	}
	
	public String getStringParam(String key)
	{
		return params.getParamStringValue(key);
	}
	
	public String getStringQueryParam(String key)
	{
		return queryParams.getParamStringValue(key);
	}
	
	public String getStringPostParam(String key)
	{
		return postParams.getParamStringValue(key);
	}
	
	public String getStringCookieParam(String key)
	{
		return cookieParams.getParamStringValue(key);
	}
	
	public boolean hasParam(String key)
	{
		return params.hasParam(key);
	}
	
	public boolean hasQueryParam(String key)
	{
		return queryParams.hasParam(key);
	}
	
	public boolean hasPostParam(String key)
	{
		return postParams.hasParam(key);
	}
	
	public boolean hasCookieParam(String key)
	{
		return cookieParams.hasParam(key);
	}
	
	protected void writeAndFlushOK(byte[] bytes)
	{
		writeAndFlush(HttpResponseStatus.OK, bytes);
	}
	
	protected void writeAndFlush(HttpResponseStatus status, byte[] bytes)
	{
		ByteBuf resp = channelHandlerContext.alloc().buffer(bytes.length);
		resp.writeBytes(bytes);
		channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, resp)).addListener(ChannelFutureListener.CLOSE);
	}
	
}
