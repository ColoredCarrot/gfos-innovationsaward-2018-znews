package de.znews.server.config;

import de.znews.server.ZNews;
import de.znews.server.dao.DataAccessConfiguration;
import de.znews.server.dao.file.FileDataAccessConfiguration;
import de.znews.server.static_web.StaticWeb;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class ZNewsConfiguration
{
	
	@Getter(AccessLevel.NONE)
	private final Properties              props;
	private final DataAccessConfiguration dataAccessConfig;
	private final StaticWeb.Config        staticWebConfig;
	
	public ZNewsConfiguration(ZNews znews, File file) throws IOException
	{
		
		props = new Properties();
		try (InputStream in = new BufferedInputStream(new FileInputStream(file)))
		{
			props.load(in);
		}
		
		switch (props.getProperty("data.method"))
		{
		case "file":
			dataAccessConfig = new FileDataAccessConfiguration(znews);
			break;
		default:
			throw new IllegalArgumentException("Unknown data storage method: " + props.getProperty("data.method"));
		}
		
		staticWebConfig = new StaticWeb.Config();
		staticWebConfig.setEnableCaching(getBoolean("cache.enabled"));
		staticWebConfig.setErr404Path(props.getProperty("err_docs.404"));
		staticWebConfig.setCacheSize(getInt("cache.size"));
		
	}
	
	public int getPort()
	{
		return getInt("port");
	}
	
	private int getInt(String key)
	{
		return Integer.parseInt(props.getProperty(key));
	}
	
	private boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(props.getProperty(key));
	}
	
}
