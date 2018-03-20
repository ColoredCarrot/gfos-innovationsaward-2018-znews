package de.znews.server.config;

import de.znews.server.Log;
import de.znews.server.ZNews;
import de.znews.server.dao.DataAccessConfiguration;
import de.znews.server.dao.file.FileDataAccessConfiguration;
import de.znews.server.dao.mysql.MySQLDataAccessConfiguration;
import de.znews.server.emai_reg.EmailConfig;
import de.znews.server.static_web.StaticWeb;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.StringJoiner;

@Getter
public class ZNewsConfiguration
{
    
    @Getter(AccessLevel.NONE)
    private final Properties              props;
    private final DataAccessConfiguration dataAccessConfig;
    private final StaticWeb.Config        staticWebConfig;
    private final EmailConfig             emailConfig;
    
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
        case "mysql":
            dataAccessConfig = new MySQLDataAccessConfiguration(znews);
            break;
        default:
            throw new IllegalArgumentException("Unknown data storage method: " + props.getProperty("data.method"));
        }
        
        staticWebConfig = new StaticWeb.Config();
        staticWebConfig.setEnableCaching(getBoolean("cache.enabled"));
        staticWebConfig.setErr404Path(props.getProperty("err_docs.404"));
        staticWebConfig.setCacheSize(getInt("cache.size"));
        
        emailConfig = new EmailConfig();
        emailConfig.setPort(getInt("email.port"));
        emailConfig.setHost(props.getProperty("email.host"));
        emailConfig.setFrom(props.getProperty("email.from"));
        emailConfig.setAuth(getBoolean("email.auth"));
        emailConfig.setAuthUsr(props.getProperty("email.auth.usr"));
        emailConfig.setAuthPw(props.getProperty("email.auth.pw"));
        emailConfig.setProtocol(EmailConfig.Protocol.valueOf(props.getProperty("email.protocol").toUpperCase(Locale.ENGLISH)));
        emailConfig.setDebug(getBoolean("email.debug"));
        
        emailConfig.setTemplatePathDoubleOptIn(props.getProperty("email.templates.double-opt-in"));
        emailConfig.setTemplatePathNewNewsletter(props.getProperty("email.templates.new-newsletter"));
        
    }
    
    public int getPort()
    {
        return getInt("port");
    }
    
    public boolean getEnableJSONPrettyPrinting()
    {
        return getBoolean("pretty-print-json");
    }
    
    public String getExternalAddress()
    {
        return props.getProperty("external-address");
    }
    
    public boolean getExternalAddressAddPort()
    {
        return getBoolean("external-address-add-port");
    }
    
    public String getFullExternalAddress()
    {
        String a = getExternalAddress();
        if (getExternalAddressAddPort())
            a += ":" + getPort();
        return a;
    }
    
    private int getInt(String key)
    {
        return Integer.parseInt(props.getProperty(key));
    }
    
    private boolean getBoolean(String key)
    {
        return Boolean.parseBoolean(props.getProperty(key));
    }
    
    public Properties props()
    {
        return props;
    }
    
    public void printDebug()
    {
        Log.debug(() ->
        {
            StringJoiner lines = new StringJoiner("\n", "==========================\n", "\n==========================");
            lines.add("Port: " + getPort());
            lines.add("Enable JSON pretty-printing: " + getEnableJSONPrettyPrinting());
            lines.add("Enable StaticWeb caching: " + getStaticWebConfig().isEnableCaching());
            if (getStaticWebConfig().isEnableCaching())
                lines.add("StaticWeb cache size: " + getStaticWebConfig().getCacheSize());
            lines.add("Email host: " + getEmailConfig().getHost());
            lines.add("Email port: " + getEmailConfig().getPort());
            lines.add("Email auth user: " + getEmailConfig().getAuthUsr());
            if (getEmailConfig().getAuthPw() != null)
                lines.add("Email auth password: ***");
            lines.add("Email protocol: " + getEmailConfig().getProtocol());
            return "Configuration:\n" + lines;
        });
    }
    
}
