package de.znews.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public class Main
{
	
	@Getter
	private static ZNews zNews;
	
	public static void main(String[] args) throws IOException
	{
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown complete! Have a nice day ;-)")));
		
		zNews = new ZNews();
		
		zNews.startServer();
		
		readConsole(zNews);
		
	}
	
	private static void readConsole(ZNews znews) throws IOException
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
					znews.startServer();
					try
					{
						readConsole(znews);
					}
					catch (IOException e)
					{
						throw new UncheckedIOException(e);
					}
				});
				break;
			}
		}
		
	}
	
}
