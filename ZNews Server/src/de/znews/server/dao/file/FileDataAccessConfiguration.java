package de.znews.server.dao.file;

import de.znews.server.dao.DataAccess;
import de.znews.server.dao.DataAccessConfiguration;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FileDataAccessConfiguration extends DataAccessConfiguration
{
	
	private final File file;
	
	public FileDataAccessConfiguration()
	{
		this(new File("data.ser"));
	}
	
	@Override
	protected DataAccess newDataAccess()
	{
		return new FileDataAccess(file);
	}
	
}
