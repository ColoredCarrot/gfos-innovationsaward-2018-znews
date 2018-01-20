package de.znews.server;

import de.znews.server.auth.Admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

public class Main
{
    
    private static ZNews znews;
    
    public static void main(String[] args) throws IOException
    {
    
        System.out.println("(Begin)");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("(End)")));
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            Thread.UncaughtExceptionHandler javaDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                Log.ifReadyOrElse(l -> l.err("Unhandled exception. Future behaviour is unspecified. It is recommended to restart (NOT using \"restart\") ", e),
                        () -> javaDefaultUncaughtExceptionHandler.uncaughtException(t, e));
            }
        });
        Thread.currentThread().setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
        
        znews = new ZNews();
        
        znews.startServer();
        
        readConsole();
        
    }
    
    private static void readConsole() throws IOException
    {
        
        BufferedReader sinReader = new BufferedReader(new InputStreamReader(System.in));
        
        String command;
        while ((command = sinReader.readLine()) != null)
        {
            if (command.equalsIgnoreCase("end"))
            {
                Log.out("Shutting down...");
                znews.shutdown();
                try
                {
                    znews.awaitTermination(10, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    Log.err("Could not await server termination", e);
                }
                finally
                {
                    Log.out("Shutdown complete! Have a nice day ;-)");
                    znews.shutdownLogSystem();
                }
                break;
            }
            if (command.equalsIgnoreCase("restart"))
            {
                Log.out("Restarting...");
                znews.shutdown();
                try
                {
                    znews.awaitTermination(10, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    Log.err("Could not await server termination", e);
                }
                new Thread(() ->
                {
                    try
                    {
                        for (int i = 5; i > 0; i--)
                        {
                            Log.out("Starting in " + i + " second" + (i != 1 ? "s" : ""));
                            Thread.sleep(1000);
                        }
                    }
                    catch (InterruptedException ignored)
                    {
                    }
                    try
                    {
                        znews = new ZNews();
                        znews.startServer();
                        readConsole();
                    }
                    catch (IOException e)
                    {
                        throw new UncheckedIOException(e);
                    }
                }).start();
                break;
            }
            if (command.equalsIgnoreCase("restart server") || command.equalsIgnoreCase("srestart"))
            {
                znews.stopServer();
                try
                {
                    znews.server.awaitShutdown();
                }
                catch (InterruptedException e)
                {
                    Log.err("Could not await server termination", e);
                }
                znews.startServer();
            }
            if (command.equalsIgnoreCase("reset caches") || command.equalsIgnoreCase("rs"))
            {
                znews.staticWeb.purgeCache();
                Log.out("[Command] Caches reset");
            }
            else if (command.startsWith("addadmin "))
                cmdAddAdmin(command);
            
        }
        
    }
    
    private static void cmdAddAdmin(String command)
    {
        
        String[] args     = command.split(" ");
        String   email    = args[1];
        String   name     = args[2];
        String   password = args[3];
        
        Log.out("[Command] Add admin...");
        Log.out("[Command]  - Email: " + email);
        Log.out("[Command]  - Name: " + name);
        Log.out("[Command]  - Password: " + password);
        
        Admin admin = znews.authenticator.addAdmin(email, name, password);
        
        Log.out("[Command] Done! Generated unique ID: " + admin.getUniqueId());
        
    }
    
    @Deprecated
    public static ZNews getZnews()
    {
        return Main.znews;
    }
    
}
