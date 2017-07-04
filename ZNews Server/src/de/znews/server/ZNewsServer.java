package de.znews.server;

import de.znews.server.netty.ResourceProviderHandler;
import de.znews.server.netty.ZNewsBaseRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ZNewsServer extends Thread
{
	
	private final int port;
	
	private Channel channel;
	
	@Override
	public void run()
	{
		
		EventLoopGroup bossGroup   = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try
		{
			
			ServerBootstrap server = new ServerBootstrap();
			
			server.group(bossGroup, workerGroup)
			      .channel(NioServerSocketChannel.class)
			      .childHandler(new ChannelInitializer<SocketChannel>()
			      {
				      @Override
				      protected void initChannel(SocketChannel ch)
				      {
					      // Init the pipeline of every new connection (or channel)
					      ch.pipeline().addLast("httpcodec", new HttpServerCodec());  // HttpRequestDecoder and HttpResponseEncoder
					      ch.pipeline().addLast("httpaggregator", new HttpObjectAggregator(65536));  // Aggregate framed messages
					      ch.pipeline().addLast(new ChunkedWriteHandler());
					      ch.pipeline().addLast(new ZNewsBaseRequestHandler());
					      ch.pipeline().addLast(new ResourceProviderHandler());
				      }
			      });
			
			channel = server.bind(port).sync().channel();
			
			System.out.println("Server started, end with \"end\" (without quotation marks)");
			
			//Log.info("Server started, end with \"end\" (without quotation marks)");
			//Log.de bug("Waiting for connections...");
			
			try
			{
				channel.closeFuture().sync();
			}
			catch (InterruptedException ignored)
			{
			}
			
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			
			AtomicInteger shutdownCount = new AtomicInteger(0);
			GenericFutureListener<? extends Future<Object>> f = a ->
			{
				//Log.debug("Closing IO-Threads (" + shutdownCount.incrementAndGet() + "/2)");
			};
			
			workerGroup.shutdownGracefully().addListener(f);
			bossGroup.shutdownGracefully().addListener(f);
			
		}
		
	}
	
	public void shutdownGracefully()
	{
		if (channel != null)
			channel.close().addListener(f -> channel = null);
	}
	
}
