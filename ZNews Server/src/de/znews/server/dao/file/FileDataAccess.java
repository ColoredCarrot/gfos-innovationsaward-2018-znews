package de.znews.server.dao.file;

import de.znews.server.auth.Authenticator;
import de.znews.server.dao.DataAccess;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.RegistrationList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDataAccess extends DataAccess
{
	
	private final File registrationsFile;
	private final File authFile;
	private final File newslettersFile;
	
	@Override
	public void storeRegistrationList(RegistrationList list) throws IOException
	{
		storeSerializable(list, registrationsFile);
	}
	
	@Override
	public RegistrationList queryRegistrationList() throws IOException
	{
		return querySerializable(registrationsFile, RegistrationList::new);
	}
	
	@Override
	public void storeAuthenticator(Authenticator authenticator) throws IOException
	{
		storeSerializable(authenticator, authFile);
	}
	
	@Override
	public Authenticator queryAuthenticator() throws IOException
	{
		return querySerializable(authFile, Authenticator::new);
	}
	
	@Override
	public void storeNewsletterManager(NewsletterManager newsletterManager) throws IOException
	{
		storeSerializable(newsletterManager, newslettersFile);
	}
	
	@Override
	public NewsletterManager queryNewsletterManager() throws IOException
	{
		return querySerializable(newslettersFile, NewsletterManager::new);
	}
	
	private void storeSerializable(Serializable serializable, File file) throws IOException
	{
		try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
		{
			out.writeObject(serializable);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T querySerializable(File file, Supplier<T> ifNotExistsGet) throws IOException
	{
		if (!file.exists())
			return ifNotExistsGet.get();
		try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))))
		{
			return (T) in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException("Corrupted file: " + file.getAbsolutePath(), e);
		}
	}
	
	@Override
	@Deprecated
	public void doClose()
	{
	}
	
}
