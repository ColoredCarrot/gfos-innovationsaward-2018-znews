package de.znews.server;

import de.znews.server.auth.Admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public class Main
{
    
    private static ZNews znews;
    
    public static void main(String[] args) throws IOException
    {
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown complete! Have a nice day ;-)")));
        
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
                znews.stopServer(znews::shutdownLogSystem);
                break;
            }
            if (command.equalsIgnoreCase("restart"))
            {
                Log.out("Restarting...");
                znews.stopServer(() ->
                {
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
                });
                break;
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
