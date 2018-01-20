package de.znews.server;

import com.coloredcarrot.loggingapi.loggers.Loggers;
import de.znews.server.auth.Authenticator;
import de.znews.server.config.ZNewsConfiguration;
import de.znews.server.emai_reg.EmailSender;
import de.znews.server.emai_reg.EmailTemplates;
import de.znews.server.netty.ZNewsNettyServer;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.RegistrationList;
import de.znews.server.sessions.SessionManager;
import de.znews.server.static_web.StaticWeb;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZNews
{
    
    public final ZNewsConfiguration config;
    public final RegistrationList   registrationList;
    public final Authenticator      authenticator;
    public final SessionManager     sessionManager;
    public final NewsletterManager  newsletterManager;
    public final StaticWeb          staticWeb;
    public final EmailSender        emailSender;
    public final EmailTemplates     emailTemplates;
    
    public volatile ZNewsNettyServer server;
    
    private volatile CountDownLatch stopServerLatch = new CountDownLatch(0);
    private volatile CountDownLatch shutdownLatch = new CountDownLatch(0);
    
    private boolean valid;
    
    public ZNews() throws IOException
    {
        
        // Setup configuration
        File cfgFile = new File("config.properties");
        if (!cfgFile.exists())
        {
            try (InputStream in = Main.class.getResourceAsStream("/resources/config.properties");
                 OutputStream out = new BufferedOutputStream(new FileOutputStream(cfgFile)))
            {
                int read;
                while ((read = in.read()) != -1)
                    out.write(read);
            }
        }
        // Initialize Logger
        config = new ZNewsConfiguration(this, cfgFile);
        
        Log.setLogger(Loggers.build(config.props(), System.out));
        
        config.printDebug();
        
        // Load registrationList
        registrationList = config.getDataAccessConfig().access().queryRegistrationList();
        
        // Load newsletters
        newsletterManager = config.getDataAccessConfig().access().queryNewsletterManager();
        
        // Load authenticator (list of admins)
        authenticator = config.getDataAccessConfig().access().queryAuthenticator();
        
        sessionManager = new SessionManager(this, authenticator);
        
        staticWeb = new StaticWeb(new File("static_web"), config.getStaticWebConfig());
        
        emailSender = new EmailSender(this);
        emailTemplates = new EmailTemplates(this);
    
        valid = true;
    }
    
    public void startServer()
    {
        if (!valid)
            throw new IllegalStateException("ZNews instance is not valid");
        if (server != null)
            throw new IllegalStateException("Server already started");
        server = new ZNewsNettyServer(this, config.getPort());
        Log.dev("Starting server thread");
        server.start();
    }
    
    public void stopServer()
    {
        if (!valid)
            throw new IllegalStateException("ZNews instance is not valid");
        if (server == null)
            throw new IllegalStateException("Server not started");
        stopServerLatch = new CountDownLatch(1);
        Log.debug("Stopping server...");
        server.shutdownGracefully();
        server.onShutdown(() ->
        {
            server = null;
            sessionManager.invalidateAllSessions();
            Log.debug("Server stopped");
            stopServerLatch.countDown();
            shutdownLatch.countDown();
        });
    }
    
    public boolean isServerStopping()
    {
        return stopServerLatch.getCount() == 1;
    }
    
    public void awaitServerStop() throws InterruptedException
    {
        if (!valid)
            throw new IllegalStateException("ZNews instance is not valid");
        stopServerLatch.await();
    }
    
    public boolean awaitServerStop(long timeout, TimeUnit unit) throws InterruptedException
    {
        return stopServerLatch.await(timeout, unit);
    }
    
    public void shutdown()
    {
        if (!valid)
            throw new IllegalStateException("ZNews instance is not valid");
        shutdownLatch = new CountDownLatch(2);
        stopServer();
        saveAll();
        valid = false;
    }
    
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        long start = System.nanoTime();
        shutdownLatch.await();
        long remainingTimeoutNanos = unit.toNanos(timeout) - (System.nanoTime() - start);
        awaitNettyGlobalThreadsTermination(remainingTimeoutNanos);
    }
    
    public void saveAll()
    {
        new Thread(() ->
        {
            try
            {
                config.getDataAccessConfig().access().storeRegistrationList(registrationList);
                config.getDataAccessConfig().access().storeAuthenticator(authenticator);
                config.getDataAccessConfig().access().storeNewsletterManager(newsletterManager);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
            finally
            {
                try
                {
                    config.getDataAccessConfig().closeAccess();
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
                finally
                {
                    shutdownLatch.countDown();
                }
            }
        }).start();
    }
    
    public void shutdownLogSystem()
    {
        Log.dev("Shutting down log system...");
        new Thread(Log::shutdown).start();
    }
    
    /**
     * Awaits the shutdown of {@link ThreadDeathWatcher}
     * and {@link GlobalEventExecutor} using
     * {@link ThreadDeathWatcher#awaitInactivity(long, TimeUnit) ThreadDeathWatcher.awaitInactivity}
     * and {@link GlobalEventExecutor#awaitInactivity(long, TimeUnit) GlobalEventExecutor.INSTANCE.awaitInactivity}
     * respectively.
     *
     * @param timeoutNanos The maximum amount of nanoseconds this method may block
     */
    protected void awaitNettyGlobalThreadsTermination(long timeoutNanos) throws InterruptedException
    {
        long start = System.nanoTime();
        try
        {
            ThreadDeathWatcher.awaitInactivity(timeoutNanos, TimeUnit.NANOSECONDS);
        }
        finally
        {
            timeoutNanos -= System.currentTimeMillis() - start;
            start = 0L;
            try
            {
                GlobalEventExecutor.INSTANCE.awaitInactivity(timeoutNanos, TimeUnit.MILLISECONDS);
            }
            catch (IllegalStateException ignored)
            {
                // Occurs when there was no globalEventExecutor thread
            }
        }
    }
    
}
