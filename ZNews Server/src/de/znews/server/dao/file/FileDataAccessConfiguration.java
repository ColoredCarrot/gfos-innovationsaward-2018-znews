package de.znews.server.dao.file;

import de.znews.server.dao.DataAccess;
import de.znews.server.dao.DataAccessConfiguration;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FileDataAccessConfiguration extends DataAccessConfiguration
{
	
	private final File registrationsFile;
	private final File authFile;
	private final File newslettersFile;
	
	public FileDataAccessConfiguration()
	{
		this(new File("registrations.json"), new File("auth.json"), new File("newsletters.json"));
	}
	
	@Override
	protected DataAccess newDataAccess()
	{
		return new FileDataAccess(registrationsFile, authFile, newslettersFile);
	}
	
}
