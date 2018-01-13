package de.znews.server.netty;

import de.znews.server.Log;
import de.znews.server.ZNews;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.BindException;
import java.util.concurrent.atomic.AtomicInteger;

public class ZNewsNettyServer extends Thread
{
	
	private final ZNews znews;
	private final int   port;
	
	public ZNewsNettyServer(ZNews znews, int port)
	{
		super("server-thread");
		this.znews = znews;
		this.port = port;
	}
	
	private Channel channel;
	
	private EventLoopGroup workerGroup;
	
	@Override
	public void run()
	{
		
		EventLoopGroup bossGroup   = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		
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
					      ch.pipeline().addLast("codec", new HttpServerCodec());  // HttpRequestDecoder and HttpResponseEncoder
					      ch.pipeline().addLast("compressor", new HttpContentCompressor());  // GZIP compression
					      ch.pipeline().addLast("decompressor", new HttpContentDecompressor());  // GZIP decompression
					      ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));  // Aggregate framed messages
					      ch.pipeline().addLast("chunking", new ChunkedWriteHandler());  // Handle chunked input (e.g. ChunkedFile)
					      ch.pipeline().addLast(new FullHttpRequestDecoder());  // Decode FullHttpRequest to URIFragment
					      ch.pipeline().addLast(new ResourceProviderHandler(znews));
				      }
			      });
            
            try
            {
                channel = server.bind(port).sync().channel();
            }
            catch (Throwable e)
            {
                //noinspection ConstantConditions
                if (!(e instanceof BindException))
                    //noinspection ProhibitedExceptionThrown
                    throw e;
                // Failed to bind to port
                Log.fatal("Failed to bind to port " + port);
                znews.shutdownLogSystem();
                System.exit(-1);
            }
            
            Log.out("Server started, end with \"end\" (without quotation marks)");
			Log.debug("Listening for connections...");
			
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
				Log.debug("Closing IO-Threads (" + shutdownCount.incrementAndGet() + "/2)");
				if (shutdownCount.get() == 2)
                    znews.shutdownLatch.countDown();
			};
			
			workerGroup.shutdownGracefully().addListener(f);
			bossGroup.shutdownGracefully().addListener(f);
			
		}
		
	}
	
	public void shutdownGracefully(Runnable callback)
	{
		if (channel != null)
			channel.close().addListener(f ->
			{
				channel = null;
				znews.staticWeb.purgeCache();
				if (callback != null)
					callback.run();
			});
	}
    
    public EventLoopGroup getWorkerGroup()
    {
        return workerGroup;
    }
    
}
