package de.znews.server.dao.mysql;

import de.znews.server.ZNews;
import de.znews.server.dao.DataAccess;
import de.znews.server.dao.DataAccessConfiguration;

public class MySQLDataAccessConfiguration extends DataAccessConfiguration
{
    
    private final ZNews znews;
    final String host;
    final String port;
    final String usr;
    final String pw;
    
    public MySQLDataAccessConfiguration(ZNews znews, String host, String port, String usr, String pw)
    {
        this.znews = znews;
        this.host = host;
        this.port = port;
        this.usr = usr;
        this.pw = pw;
    }
    
    @Override
    protected DataAccess newDataAccess()
    {
        return null;
    }
    
}
