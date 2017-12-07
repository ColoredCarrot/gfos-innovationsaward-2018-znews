package de.znews.server.resources;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.HttpHeaderNames;
import lombok.Getter;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RequestResponse
{
    
    public static RequestResponse ok(byte[] data)
    {
        return new RequestResponse(HttpResponseStatus.OK, data);
    }
    
    public static RequestResponse ok(String data)
    {
        return new RequestResponse(HttpResponseStatus.OK, data.getBytes(StandardCharsets.UTF_8));
    }
    
    private byte[]             data;
	private HttpVersion        httpVersion;
	private HttpResponseStatus status;
	@Nullable
	private String             contentType;
	private Set<Cookie> cookies = new HashSet<>();
	
	public RequestResponse(byte[] data)
	{
		this(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, data);
	}
	
	public RequestResponse(@Nullable String contentType, byte[] data)
	{
		this(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, contentType, data);
	}
	
	public RequestResponse(HttpResponseStatus status, byte[] data)
	{
		this(HttpVersion.HTTP_1_1, status, data);
	}
	
	public RequestResponse(HttpVersion httpVersion, HttpResponseStatus status, byte[] data)
	{
		this(httpVersion, status, null, data);
	}
	
	public RequestResponse(HttpVersion httpVersion, HttpResponseStatus status, @Nullable String contentType, byte[] data)
	{
		this.data = data;
		this.httpVersion = httpVersion;
		this.status = status;
		this.contentType = contentType;
	}
	
	public RequestResponse setData(byte[] data)
	{
		this.data = data;
		return this;
	}
	
	public RequestResponse setHttpVersion(HttpVersion httpVersion)
	{
		this.httpVersion = httpVersion;
		return this;
	}
	
	public RequestResponse setStatus(HttpResponseStatus status)
	{
		this.status = status;
		return this;
	}
	
	public RequestResponse setContentType(@Nullable String contentType)
	{
		this.contentType = contentType;
		return this;
	}
    
    public Set<Cookie> getCookies()
    {
        if (cookies == null)
            cookies = new HashSet<>();
        return cookies;
    }
    
    public void setCookies(Set<Cookie> cookies)
    {
        this.cookies = cookies;
    }
    
    public void addCookie(Cookie cookie)
    {
        getCookies().add(cookie);
    }
    
    public void respond(ChannelHandlerContext channelHandlerContext)
	{
		
		ByteBuf byteBuf = channelHandlerContext.alloc().buffer(data.length);
		byteBuf.writeBytes(data);
		
		DefaultFullHttpResponse resp = new DefaultFullHttpResponse(httpVersion, status, byteBuf);
		
		if (contentType != null)
		{
			DefaultHttpHeaders headers = (DefaultHttpHeaders) resp.headers();
			headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
  
		if (cookies != null && !cookies.isEmpty())
            resp.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookies));
		
		channelHandlerContext.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
		
		data = null;
		
	}
	
}
