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

        // Application will always start with "(Begin)" and end with "(End)"
        System.out.println("(Begin)");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("(End)")));

        // Replace uncaught exception handler to log to custom Logger, if ready,
        // otherwise, forward to default default uncaught exception handler

        Thread.UncaughtExceptionHandler javaDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Log.ifReadyOrElse(l -> l.err("Unhandled exception. Future behaviour is unspecified. It is recommended to restart (NOT using \"restart\") ", e),
                () ->
                {
                    if (javaDefaultUncaughtExceptionHandler != null)
                        javaDefaultUncaughtExceptionHandler.uncaughtException(t, e);
                    else
                        e.printStackTrace();
                }));
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
                // Shutdown server same as "end"
                // Then, re-instantiate ZNews instance and initiate start

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
                // A "light" restart, only affects server
                // ZNews instance does not change

                znews.stopServer();
                try
                {
                    znews.awaitServerStop();
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
            else if (command.startsWith("addadmin"))
                cmdAddAdmin(command);
            
        }
        
    }
    
    private static void cmdAddAdmin(String command)
    {
        if (command.equals("addadmin"))
        {
            // Display help
            Log.out("[Command] Syntax: addadmin <email> <name> <password>");
            return;
        }
        
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
