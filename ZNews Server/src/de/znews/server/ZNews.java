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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;

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
    
    public ZNewsNettyServer server;
    
    public CountDownLatch shutdownLatch;
    
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
        
    }
    
    public void startServer()
    {
        
        if (server != null)
            return;
        
        server = new ZNewsNettyServer(this, config.getPort());
        
        Log.out("Starting server on port " + config.getPort() + "...");
        
        server.start();
        
    }
    
    public void stopServer()
    {
        stopServer(null);
    }
    
    public void stopServer(Runnable callback)
    {
        // FINDME: Here is defined the number znews.shutdownLatch.countDown() needs to be called
        shutdownLatch = new CountDownLatch(3);
        if (server != null)
        {
            server.shutdownGracefully(() ->
            {
                server = null;
                shutdownLatch.countDown();
            });
        }
        saveAll();
        new Thread(() ->
        {
            try
            {
                shutdownLatch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.out("Shutdown complete! Have a nice day ;-)");
            callback.run();
        }).start();
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
        new Thread(Log::shutdown).start();
    }
    
}
