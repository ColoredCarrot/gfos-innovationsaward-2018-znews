package de.znews.server.staticweb;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class StaticWeb
{
	
	private File dir;
	
	public StaticWeb(File dir)
	{
		this.dir = dir;
	}
	
	private WeakReference<byte[]> err404NotFound;
	
	private byte[] load(File file) throws IOException
	{
		byte[] result;
		try (InputStream in = new BufferedInputStream(new FileInputStream(file));
		     ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			int read;
			while ((read = in.read()) != -1)
				out.write(read);
			result = out.toByteArray();
		}
		return result;
	}
	
	private void loadErr404NotFound() throws IOException
	{
		err404NotFound = new WeakReference<>(load(new File(dir, "404notfound.html")));
	}
	
	public void send404NotFound(ChannelHandlerContext ctx) throws IOException
	{
		byte[] data = err404NotFound != null ? err404NotFound.get() : null;
		if (data == null)
		{
			loadErr404NotFound();
			data = err404NotFound.get();
			assert data != null;
		}
		ByteBuf byteBuf = ctx.alloc().buffer(data.length);
		byteBuf.writeBytes(data);
		ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, byteBuf));
	}
	
	/*public void send404NotFound(ChannelHandlerContext ctx) throws IOException
	{
		ctx.writeAndFlush(new ChunkedFile(new File(dir, "404notfound.html")));
	}*/
	
}
