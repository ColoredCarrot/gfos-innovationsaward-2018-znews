package de.znews.server.newsletter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsletterManager implements Serializable
{
	
	private static final long serialVersionUID = 592773864822928724L;
	
	// Ordered latest newsletter first
	private List<Newsletter> newsletters = new ArrayList<>();
	
	public void addNewsletter(Newsletter newsletter)
	{
		newsletters.add(0, newsletter);
	}
	
	public void getLatestNewsletters(Newsletter[] buffer)
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = newsletters.get(i);
	}
	
}
