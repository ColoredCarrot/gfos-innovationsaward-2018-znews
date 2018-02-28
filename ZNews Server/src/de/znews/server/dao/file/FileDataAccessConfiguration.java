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
    private final File nprFolder;
    
    public FileDataAccessConfiguration(ZNews znews)
    {
        this(znews, new File("registrations.json"), new File("auth.json"), new File("newsletters.json"), new File("publication_results"));
    }
    
    public FileDataAccessConfiguration(ZNews znews, File registrationsFile, File authFile, File newslettersFile, File nprFolder)
    {
        this.znews = znews;
        this.registrationsFile = registrationsFile;
        this.authFile = authFile;
        this.newslettersFile = newslettersFile;
        this.nprFolder = nprFolder;
    }
    
    @Override
    protected DataAccess newDataAccess()
    {
        return new FileDataAccess(znews, registrationsFile, authFile, newslettersFile, nprFolder);
    }
    
}
