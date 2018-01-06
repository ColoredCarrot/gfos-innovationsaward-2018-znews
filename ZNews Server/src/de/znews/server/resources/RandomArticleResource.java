package de.znews.server.resources;

import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.HttpHeaderNames;

import java.nio.charset.StandardCharsets;

public class RandomArticleResource extends Resource
{
    
    public RandomArticleResource(ZNews znews)
    {
        super(znews, URIFragment.fromURI("random_article"));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        
        String randomNid = znews.newsletterManager.getRandomNewsletterId();
        
        // This is REALLY BAD
        // We need redesign RequestResponse to allow for custom headers
        // right now, we override #respond to insert the "location" header
        return new RequestResponse(HttpResponseStatus.TEMPORARY_REDIRECT, ("/view?nid=" + randomNid).getBytes(StandardCharsets.UTF_8))
        {
            @Override
            public void respond(ChannelHandlerContext channelHandlerContext)
            {
                final byte[] _data = ("/view?nid=" + randomNid).getBytes(StandardCharsets.UTF_8);
                
                ByteBuf byteBuf = channelHandlerContext.alloc().buffer(_data.length);
                byteBuf.writeBytes(_data);
                
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT, byteBuf);
                
                DefaultHttpHeaders headers = (DefaultHttpHeaders) resp.headers();
                headers.set(HttpHeaderNames.LOCATION, "/view?nid=" + randomNid);
                
                channelHandlerContext.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
            }
        };
        
    }
    
}
