package de.znews.server.dao;

import lombok.SneakyThrows;

import javax.annotation.Nullable;

public abstract class DataAccessConfiguration
{
	
	@Nullable
	private DataAccess dataAccess;
	
	public synchronized DataAccess access()
	{
		if (dataAccess == null || dataAccess.isClosed())
			initDataAccess();
		return dataAccess;
	}
	
	@SneakyThrows
	private synchronized void initDataAccess()
	{
		if (dataAccess != null)
		{
			dataAccess.close();
			dataAccess = null;
		}
		dataAccess = newDataAccess();
	}
	
	protected abstract DataAccess newDataAccess();
	
}
