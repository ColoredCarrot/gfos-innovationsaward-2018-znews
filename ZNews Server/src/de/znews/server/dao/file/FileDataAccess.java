package de.znews.server.dao.file;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.generation.JsonOutput;
import com.coloredcarrot.jsonapi.parsing.JsonInput;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import de.znews.server.auth.Authenticator;
import de.znews.server.dao.DataAccess;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.RegistrationList;
import de.znews.server.stat.NewsletterPublicationResult;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

public class FileDataAccess extends DataAccess
{
    
    private static final DateFormat nprDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss.SSSZ");
    
	private final File registrationsFile;
	private final File authFile;
	private final File newslettersFile;
	private final File nprFolder;
	
	public FileDataAccess(ZNews znews, File registrationsFile, File authFile, File newslettersFile, File nprFolder)
	{
		super(znews);
		this.registrationsFile = registrationsFile;
		this.authFile = authFile;
		this.newslettersFile = newslettersFile;
        this.nprFolder = nprFolder;
    }
	
	@Override
	public void storeRegistrationList(RegistrationList list) throws IOException
	{
		storeJsonSerializable(list, registrationsFile);
	}
	
	@Override
	public RegistrationList queryRegistrationList() throws IOException
	{
		return queryJsonSerializable(registrationsFile, RegistrationList::new, RegistrationList.class);
	}
	
	@Override
	public void storeAuthenticator(Authenticator authenticator) throws IOException
	{
		storeJsonSerializable(authenticator, authFile);
	}
	
	@Override
	public Authenticator queryAuthenticator() throws IOException
	{
		Authenticator r = queryJsonSerializable(authFile, Authenticator::new, Authenticator.class);
		// this needs to be here because, when read from JSON,
        // the znews attribute is null
		r.setZNewsInstance(getZNews());
		return r;
	}
	
	@Override
	public void storeNewsletterManager(NewsletterManager newsletterManager) throws IOException
	{
		storeJsonSerializable(newsletterManager, newslettersFile);
	}
	
	@Override
	public NewsletterManager queryNewsletterManager() throws IOException
	{
		return queryJsonSerializable(newslettersFile, NewsletterManager::new, NewsletterManager.class);
	}
	
	@Override
	public void storeNewNewsletterPublicationResult(NewsletterPublicationResult res) throws IOException
    {
	    File f;
        synchronized (nprDateFormat)  // SimpleDateFormat provides no internal synchronization
        {
            f = new File(nprFolder, nprDateFormat.format(new Date()) + ".json");
        }
        nprFolder.mkdirs();
        storeJsonSerializable(res, f);
    }
	
	private void storeJsonSerializable(JsonSerializable serializable, File file) throws IOException
    {
        try (JsonOutput out = new JsonOutput(Json.getOutputStream(new BufferedOutputStream(new FileOutputStream(file)), getZNews().config.getEnableJSONPrettyPrinting())))
        {
            out.write(serializable);
        }
    }
    
    private <T extends JsonSerializable> T queryJsonSerializable(File file, Supplier<T> ifNotExistsGet, Class<T> clazz) throws IOException
	{
		if (!file.exists())
			return ifNotExistsGet.get();
        try (JsonInput<T> in = new JsonInput<>(Json.getInputStream(new BufferedInputStream(new FileInputStream(file))), clazz))
        {
            return in.read();
        }
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
