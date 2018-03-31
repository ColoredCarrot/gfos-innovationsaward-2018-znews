package de.znews.server;

import com.coloredcarrot.loggingapi.loggers.Loggers;
import de.znews.server.auth.Authenticator;
import de.znews.server.config.ZNewsConfiguration;
import de.znews.server.emai_reg.EmailSender;
import de.znews.server.emai_reg.EmailTemplates;
import de.znews.server.lib.FileSizeUtil;
import de.znews.server.lib.JarExtractionUtil;
import de.znews.server.netty.ZNewsNettyServer;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.RegistrationList;
import de.znews.server.sessions.SessionManager;
import de.znews.server.static_web.StaticWeb;
import de.znews.server.tags.TagsList;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.*;
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
    public final TagsList           tagsList;

    public volatile ZNewsNettyServer server;

    /**
     * Blocks only when the HTTP server is currently stopping
     */
    private volatile CountDownLatch stopServerLatch = new CountDownLatch(0);
    private volatile CountDownLatch shutdownLatch   = new CountDownLatch(0);

    private boolean valid;

    public ZNews() throws IOException
    {

        // Load all the stuff n things

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

        // Unpack static_web if not already
        File staticWebFolder = new File("static_web");
        if (!staticWebFolder.exists())
            unpackStaticWebFolder(staticWebFolder);
        staticWeb = new StaticWeb(staticWebFolder, config.getStaticWebConfig());

        emailSender = new EmailSender(this);
        emailTemplates = new EmailTemplates(this);

        tagsList = new TagsList(this);

        valid = true;
    }

    /**
     * Instantiates and starts the HTTP server
     * @see ZNewsNettyServer#start()
     */
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

    /**
     * Shuts the HTTP server down gracefully
     * and invalidates all sessions afterwards.
     *
     * @implNote This method instantiates {@link #stopServerLatch} with a count of 1
     * and counts it down after the server has stopped.
     * Also, counts down {@link #shutdownLatch} after server shutdown.
     * <p>
     * This method uses {@link ZNewsNettyServer#onShutdown(Runnable)} to hook the necessary callback.
     *
     * @see ZNewsNettyServer#shutdownGracefully()
     */
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

    /**
     * Checks whether the server is currently stopping.
     * @implNote The result is computed by evaluating the count of {@link #stopServerLatch}
     * @return Whether the server is shutting down
     */
    public boolean isServerStopping()
    {
        return stopServerLatch.getCount() == 1;
    }

    /**
     * Blocks until the server has stopped.
     * @throws InterruptedException If interrupted
     * @see CountDownLatch#await()
     */
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

    /**
     * Begins shutting down the server and saving all data.
     * Calling this method will immediately <b>invalidate this ZNews instance</b>,
     * rendering it unusable.
     * @see #stopServer()
     * @see #saveAll()
     */
    public void shutdown()
    {
        if (!valid)
            throw new IllegalStateException("ZNews instance is not valid");
        shutdownLatch = new CountDownLatch(2);
        stopServer();
        saveAll();
        valid = false;
    }

    /**
     * Blocks until ZNews has shut down on a global scale.
     * After this method returns normally,
     * the HTTP server will no longer be running,
     * and there will not be any lingering threads by Netty.
     * @param timeout The timeout
     * @param unit The unit of timeout
     * @throws InterruptedException If interrupted
     */
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        long start = System.nanoTime();
        shutdownLatch.await();
        long remainingTimeoutNanos = unit.toNanos(timeout) - (System.nanoTime() - start);
        awaitNettyGlobalThreadsTermination(remainingTimeoutNanos);
    }

    /**
     * Starts a new Thread tasked with saving the
     * registration list, authenticator, newsletter manager, and tags list
     */
    public void saveAll()
    {
        new Thread(() ->
        {
            try
            {
                config.getDataAccessConfig().access().storeRegistrationList(registrationList);
                config.getDataAccessConfig().access().storeAuthenticator(authenticator);
                config.getDataAccessConfig().access().storeNewsletterManager(newsletterManager);
                tagsList.save();
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

    /**
     * Starts a new Thread which will call {@link Log#shutdown()}
     */
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
            timeoutNanos -= System.nanoTime() - start;
            start = 0L;
            try
            {
                GlobalEventExecutor.INSTANCE.awaitInactivity(timeoutNanos, TimeUnit.NANOSECONDS);
            }
            catch (IllegalStateException ignored)
            {
                // Occurs when there was no globalEventExecutor thread
            }
        }
    }

    private void unpackStaticWebFolder(File folder)
    {
        try
        {
            long bytesUnpacked = JarExtractionUtil.extractDirectory("static_web", folder.toPath());
            Log.out("Unpacked static_web (" + FileSizeUtil.displaySize(bytesUnpacked) + ")");
        }
        catch (IOException e)
        {
            Log.warn("Failed to unpack static_web folder", e);
        }
    }

}
