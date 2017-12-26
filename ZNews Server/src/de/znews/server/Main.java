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
                System.out.println("Shutting down...");
                znews.stopServer();
                break;
            }
            if (command.equalsIgnoreCase("restart"))
            {
                System.out.println("Restarting...");
                znews.stopServer(() ->
                {
                    new Thread(() ->
                    {
                        try
                        {
                            for (int i = 5; i > 0; i--)
                            {
                                System.out.println("Starting in " + i + " second(s)");
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
                System.out.println("Caches reset");
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
    
        System.out.println("Add admin...");
        System.out.println(" - Email: " + email);
        System.out.println(" - Name: " + name);
        System.out.println(" - Password: " + password);
        
        Admin admin = znews.authenticator.addAdmin(email, name, password);
        
        System.out.println("Done! Generated unique ID: " + admin.getUniqueId());
        
    }
    
    @Deprecated
    public static ZNews getZnews()
    {
        return Main.znews;
    }
    
}
