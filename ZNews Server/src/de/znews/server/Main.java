package de.znews.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public class Main
{
	
	@Getter
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
								System.out.println("Sstarting in " + i + " second(s)");
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
		}
		
	}
	
}
