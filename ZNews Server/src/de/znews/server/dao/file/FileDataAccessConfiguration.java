package de.znews.server.dao.file;

import de.znews.server.ZNews;
import de.znews.server.dao.DataAccess;
import de.znews.server.dao.DataAccessConfiguration;

import java.io.File;

public class FileDataAccessConfiguration extends DataAccessConfiguration
{
    
    private final ZNews znews;
    private final File registrationsFile;
    private final File authFile;
    private final File newslettersFile;
    
    public FileDataAccessConfiguration(ZNews znews)
    {
        this(znews, new File("registrations.json"), new File("auth.json"), new File("newsletters.json"));
    }
    
    public FileDataAccessConfiguration(ZNews znews, File registrationsFile, File authFile, File newslettersFile)
    {
        this.znews = znews;
        this.registrationsFile = registrationsFile;
        this.authFile = authFile;
        this.newslettersFile = newslettersFile;
    }
    
    @Override
    protected DataAccess newDataAccess()
    {
        return new FileDataAccess(znews, registrationsFile, authFile, newslettersFile);
    }
    
}
