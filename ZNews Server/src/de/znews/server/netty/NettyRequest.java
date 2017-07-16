package de.znews.server.netty;

import de.znews.server.uri.URIFragment;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class NettyRequest
{
	
	private final URIFragment uri;
	private final ByteBuf     post;
	private final Set<Cookie> cookies;
	
	public void releasePost()
	{
		ReferenceCountUtil.release(post);
	}
	
}
