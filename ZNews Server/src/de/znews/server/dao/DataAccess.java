package de.znews.server.dao;

import de.znews.server.auth.Authenticator;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.RegistrationList;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;

public abstract class DataAccess implements Closeable
{
	
	@Getter
	private boolean isClosed;
	
	public abstract void storeRegistrationList(RegistrationList list) throws IOException;
	
	public abstract RegistrationList queryRegistrationList() throws IOException;
	
	public abstract void storeAuthenticator(Authenticator authenticator) throws IOException;
	
	public abstract Authenticator queryAuthenticator() throws IOException;
	
	public abstract void storeNewsletterManager(NewsletterManager newsletterManager) throws IOException;
	
	public abstract NewsletterManager queryNewsletterManager() throws IOException;
	
	public abstract void doClose();
	
	@Override
	public final void close() throws IOException
	{
		if (isClosed)
			throw new IllegalStateException("Access already closed");
		doClose();
		isClosed = true;
	}
	
}
