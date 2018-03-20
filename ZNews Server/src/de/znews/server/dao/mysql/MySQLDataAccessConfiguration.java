package de.znews.server.dao.mysql;

import de.znews.server.ZNews;
import de.znews.server.dao.DataAccess;
import de.znews.server.dao.DataAccessConfiguration;

import java.util.Properties;

public class MySQLDataAccessConfiguration extends DataAccessConfiguration
{
    
    private final ZNews znews;
    
    private String host;
    private String port;
    private String usr;
    private String pw;
    
    public MySQLDataAccessConfiguration(ZNews znews)
    {
        this.znews = znews;
    }
    
    public MySQLDataAccessConfiguration(ZNews znews, String host, String port, String usr, String pw)
    {
        this.znews = znews;
        this.host = host;
        this.port = port;
        this.usr = usr;
        this.pw = pw;
    }
    
    private void loadConfig()
    {
        Properties cfg = znews.config.props();
        host = cfg.getProperty("data.mysql.host");
        port = cfg.getProperty("data.mysql.port");
        usr = cfg.getProperty("data.mysql.usr");
        pw = cfg.getProperty("data.mysql.pw");
    }
    
    public String getHost()
    {
        if (host == null)
            loadConfig();
        return host;
    }
    
    public String getPort()
    {
        if (port == null)
            loadConfig();
        return port;
    }
    
    public String getUsr()
    {
        if (usr == null)
            loadConfig();
        return usr;
    }
    
    public String getPw()
    {
        if (pw == null)
            loadConfig();
        return pw;
    }
    
    @Override
    protected DataAccess newDataAccess()
    {
        return new MySQLDataAccess(znews, this);
    }
    
}
