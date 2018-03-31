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

import java.net.BindException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
    
    private Channel        channel;
    private EventLoopGroup workerGroup;
    
    private final CountDownLatch shutdownLatch = new CountDownLatch(3);
    private volatile Runnable shutdownCallback;
    private final Object shutdownCallbackLock = new Object();
    
    @Override
    public void run()
    {
    
        Log.out("Starting server on port " + port + "...");

        // Instantiate event loop groups
        EventLoopGroup bossGroup = new NioEventLoopGroup();
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
                          ch.pipeline().addLast(new ResourceProviderHandler(znews));  // Answer requests using a Resource, falling back to StaticWeb
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
            // Shutdown event loop groups
            AtomicInteger eventLoopGroupShutdownIdx = new AtomicInteger(0);
            workerGroup.shutdownGracefully().addListener(f -> onEventLoopGroupShutdown(eventLoopGroupShutdownIdx.getAndIncrement()));
            bossGroup.shutdownGracefully().addListener(f -> onEventLoopGroupShutdown(eventLoopGroupShutdownIdx.getAndIncrement()));
            
            try
            {
                awaitShutdown();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                synchronized (shutdownCallbackLock)
                {
                    shutdownCallback.run();
                }
            }
        }
        
    }
    
    public void shutdownGracefully()
    {
        if (channel == null)
            throw new IllegalStateException("Already shut down");
        channel.close().addListener(f -> onChannelShutdown());
    }

    /**
     * Registers a shutdown listener that will be called once this server has shut down.
     * @param action The shutdown listener
     */
    public void onShutdown(Runnable action)
    {
        synchronized (shutdownCallbackLock)
        {
            if (shutdownCallback == null)
                shutdownCallback = action;
            else
            {
                Runnable oldShutdownCallback = this.shutdownCallback;
                shutdownCallback = () ->
                {
                    Throwable suppressed = null;
                    try
                    {
                        oldShutdownCallback.run();
                    }
                    catch (Throwable e)
                    {
                        suppressed = e;
                    }
                    finally
                    {
                        try
                        {
                            action.run();
                        }
                        catch (Throwable e)
                        {
                            if (suppressed != null)
                                e.addSuppressed(suppressed);
                            throw e;
                        }
                    }
                };
            }
        }
    }
    
    public void awaitShutdown() throws InterruptedException
    {
        shutdownLatch.await();
    }
    
    public void awaitShutdown(long timeout, TimeUnit unit) throws InterruptedException
    {
        shutdownLatch.await(timeout, unit);
    }
    
    public EventLoopGroup getWorkerGroup()
    {
        return workerGroup;
    }
    
    protected void onEventLoopGroupShutdown(int idx)
    {
        Log.debug("Closing IO-Threads (" + (idx + 1) + "/2)");
        shutdownLatch.countDown();
    }
    
    protected void onChannelShutdown()
    {
        channel = null;
        // Not exactly sure why we purge the cache HERE
        znews.staticWeb.purgeCache();
        shutdownLatch.countDown();
    }
    
}
