package de.znews.server.dao.file;

import de.znews.server.dao.DataAccess;
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

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDataAccess extends DataAccess
{
	
	private final File file;
	
	@Override
	public void storeRegistrationList(RegistrationList list) throws IOException
	{
		try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
		{
			out.writeObject(list);
		}
	}
	
	@Override
	public RegistrationList queryRegistrationList() throws IOException
	{
		if (!file.exists())
			return new RegistrationList();
		try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))))
		{
			return (RegistrationList) in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException("Corrupted file", e);
		}
	}
	
	@Override
	@Deprecated
	public void doClose()
	{
	}
	
}
