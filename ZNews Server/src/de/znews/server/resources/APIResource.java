package de.znews.server.resources;

import de.znews.server.uri.URIFragmentBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

//@Path("/apiv{api_version}")
public class APIResource extends Resource
{
	
	public APIResource()
	{
		// /api/v2
		super(new URIFragmentBuilder().add("api", "{api_version}").build());
	}
	
	@Override
	public void handleRequest(ChannelHandlerContext ctx, Param... params)
	{
		byte[] respBytes = ("API Version: " + params[0].getValue()).getBytes(StandardCharsets.UTF_8);
		ByteBuf resp = ctx.alloc().buffer(respBytes.length);
		resp.writeBytes(respBytes);
		ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, resp));
	}
	
	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatus(@PathParam("api_version") String apiVersion)
	{
		return String.format("{\"status\":\"OK\",\"version\":\"%s\"}", apiVersion);
	}*/
	
	
	
}
