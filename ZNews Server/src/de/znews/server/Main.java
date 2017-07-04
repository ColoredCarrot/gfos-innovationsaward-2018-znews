package de.znews.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
	
	public static void main(String[] args) throws IOException
	{
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown complete! Have a nice day ;-)")));
		
		int port = 8080;
		
		ZNewsServer server = new ZNewsServer(port);
		
		System.out.println("Starting server on port " + port + "...");
		
		server.start();
		
		BufferedReader sinReader = new BufferedReader(new InputStreamReader(System.in));
		
		String command;
		while ((command = sinReader.readLine()) != null)
		{
			if (command.equalsIgnoreCase("end"))
			{
				System.out.println("Shutting down...");
				server.shutdownGracefully();
				break;
			}
		}
		
	}
	
}
