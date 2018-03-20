package de.znews.server.dao.mysql;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.generation.JsonOutput;
import com.coloredcarrot.jsonapi.parsing.JsonInput;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import de.znews.server.ZNews;
import de.znews.server.auth.Admin;
import de.znews.server.auth.Authenticator;
import de.znews.server.dao.DataAccess;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.newsletter.NewsletterManager;
import de.znews.server.newsletter.Registration;
import de.znews.server.newsletter.RegistrationList;
import de.znews.server.stat.NewsletterPublicationResult;
import lombok.SneakyThrows;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Supplier;

public class MySQLDataAccess extends DataAccess
{
    
    private static final String DB_NAME = "znews_user_data";
    
    private static final TableSpec TABLE_AUTH = new TableSpec("auth",
            "uid CHAR(36) NOT NULL UNIQUE, " +
                    "email VARCHAR(128) NOT NULL, " +
                    "`name` VARCHAR(64) NOT NULL, " +
                    "pw_hash CHAR(60) NOT NULL");
    
    private static final TableSpec TABLE_REGISTRATIONS = new TableSpec("registrations",
            "email VARCHAR(128) NOT NULL UNIQUE, " +
                    "subbed_tags TEXT, " +
                    "date_subbed TIMESTAMP NOT NULL");
    
    private static final TableSpec TABLE_NEWSLETTERS = new TableSpec("newsletters",
            "nid CHAR(36) NOT NULL UNIQUE, " +
                    "title VARCHAR(128) NOT NULL, " +
                    "publisher CHAR(36), " +
                    "date_published TIMESTAMP, " +
                    "views MEDIUMINT UNSIGNED, " +
                    "tags TEXT, " +
                    "content MEDIUMTEXT");
    
    private static final DateFormat nprDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss.SSSZ");
    
    private final MySQLDataAccessConfiguration cfg;
    private       Connection                   connection;
    
    public MySQLDataAccess(ZNews znews, MySQLDataAccessConfiguration cfg)
    {
        super(znews);
        this.cfg = cfg;
    }
    
    @SneakyThrows
    private void initConnection()
    {
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Failed to find MySQL Driver: com.mysql.jdbc.Driver", e);
        }
        
        connection = DriverManager.getConnection("jdbc:mysql://" + cfg.host + ":" + cfg.port + "/?user=" + cfg.usr + "&password=" + cfg.pw + "&useSSL=false");
        
        try (PreparedStatement stmt = prepareStatement("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`"))
        {
            stmt.execute();
        }
        try (PreparedStatement stmt = prepareStatement("USE `" + DB_NAME + "`"))
        {
            stmt.execute();
        }
        
    }
    
    public PreparedStatement prepareStatement(String format) throws SQLException
    {
        return connection.prepareStatement(format);
    }
    
    public void ensureTable(TableSpec tableSpec) throws SQLException
    {
        try (PreparedStatement stmt = prepareStatement("CREATE TABLE IF NOT EXISTS " + tableSpec.name + " (" + tableSpec.structure + ")"))
        {
            stmt.execute();
        }
    }
    
    @Override
    public void storeRegistrationList(RegistrationList list) throws IOException
    {
        try
        {
            ensureTable(TABLE_REGISTRATIONS);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        try
        {
            connection.setAutoCommit(false);
            
            ensureTable(TABLE_REGISTRATIONS);
            
            try (PreparedStatement stmt = prepareStatement("DELETE FROM " + TABLE_REGISTRATIONS.name))
            {
                stmt.execute();
            }
            
            try (PreparedStatement stmt = prepareStatement("INSERT INTO " + TABLE_REGISTRATIONS.name + " VALUES (?, ?, ?)"))
            {
                for (Registration reg : list.snapshot())
                {
                    stmt.setString(1, reg.getEmail());
                    if (reg.getSubscribedTags() != null)
                        stmt.setString(2, String.join(";;", reg.getSubscribedTags()));
                    else
                        stmt.setString(2, null);
                    stmt.setTimestamp(3, new Timestamp(reg.getDateRegistered().getTime()));
                    
                    stmt.addBatch();
                }
                
                stmt.executeBatch();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
        }
        catch (SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException e1)
            {
                e1.addSuppressed(e);
                e = e1;
            }
            throw new IOException(e);
        }
    }
    
    @Override
    public RegistrationList queryRegistrationList() throws IOException
    {
        
        RegistrationList list = new RegistrationList();
        
        try
        {
            ensureTable(TABLE_REGISTRATIONS);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        try (PreparedStatement stmt = prepareStatement("SELECT * FROM " + TABLE_REGISTRATIONS.name))
        {
            try (ResultSet res = stmt.executeQuery())
            {
                while (res.next())
                {
                    
                    String email          = res.getString("email");
                    String tags           = res.getString("subbed_tags");
                    Date   dateRegistered = new Date(res.getTimestamp("date_subbed").getTime());
                    
                    list.addRegistration(new Registration(email, tags != null ? new HashSet<>(Arrays.asList(tags.split(";;"))) : null, dateRegistered));
                    
                }
            }
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        return list;
        
    }
    
    @Override
    public void storeAuthenticator(Authenticator authenticator) throws IOException
    {
        try
        {
            ensureTable(TABLE_AUTH);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        try (PreparedStatement stmt = prepareStatement("INSERT INTO " + TABLE_AUTH.name + " VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE email=email, `name`=`name`, pw_hash=pw_hash"))
        {
            
            for (Admin admin : authenticator.getAllAdmins())
            {
                stmt.setString(1, admin.getUniqueId().toString());
                stmt.setString(2, admin.getEmail());
                stmt.setString(3, admin.getName());
                stmt.setString(4, admin.getPasswordHash());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
    }
    
    @Override
    public Authenticator queryAuthenticator() throws IOException
    {
        Authenticator auth = new Authenticator();
        
        try
        {
            ensureTable(TABLE_AUTH);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        try (PreparedStatement stmt = prepareStatement("SELECT * FROM " + TABLE_AUTH.name))
        {
            try (ResultSet res = stmt.executeQuery())
            {
                while (res.next())
                {
                    
                    UUID   id     = UUID.fromString(res.getString("uid"));
                    String email  = res.getString("email");
                    String name   = res.getString("name");
                    String pwHash = res.getString("pw_hash");
                    
                    auth.addAdmin0(new Admin(id, email, name, pwHash));
                    
                }
            }
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        // this needs to be here because, when read from JSON,
        // the znews attribute is null
        auth.setZNewsInstance(getZNews());
        return auth;
    }
    
    @Override
    public void storeNewsletterManager(NewsletterManager nm) throws IOException
    {
        
        try
        {
            ensureTable(TABLE_NEWSLETTERS);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        try (PreparedStatement stmt = prepareStatement("INSERT INTO " + TABLE_NEWSLETTERS.name + " VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=title, publisher=publisher, date_published=date_published, views=views, tags=tags, content=content"))
        {
            
            for (Newsletter n : toIterable(nm.getLatestNewsletters().iterator()))
            {
                stmt.setString(1, n.getId());
                stmt.setString(2, n.getTitle());
                stmt.setString(3, n.getPublisher() != null ? n.getPublisher().toString() : null);
                stmt.setTimestamp(4, n.getDatePublished() != null ? new Timestamp(n.getDatePublished().getTime()) : null);
                stmt.setInt(5, (int) n.getViews());
                stmt.setString(6, String.join(";;", n.getTags()));
                stmt.setString(7, Json.toString(n.getContent()));
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
    }
    
    @Override
    public NewsletterManager queryNewsletterManager() throws IOException
    {
        
        try
        {
            ensureTable(TABLE_NEWSLETTERS);
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        NewsletterManager nm = new NewsletterManager();
        
        try (PreparedStatement stmt = prepareStatement("SELECT * FROM " + TABLE_NEWSLETTERS.name))
        {
            try (ResultSet res = stmt.executeQuery())
            {
                while (res.next())
                {
                    
                    String    nid           = res.getString("nid");
                    String    title         = res.getString("title");
                    String    publisher     = res.getString("publisher");
                    Timestamp datePublished = res.getTimestamp("date_published");
                    int       views         = res.getInt("views");
                    String[]  tags          = res.getString("tags").split(";;");
                    JsonArray content       = (JsonArray) Json.getInputStream(new StringReader(res.getString("content"))).next();
    
                    nm.addNewsletter(new Newsletter(nid, title, content, new ArrayList<>(Arrays.asList(tags)), datePublished != null, datePublished != null ? new Date(datePublished.getTime()) : null, publisher != null ? UUID
                            .fromString(publisher) : null, views));<
                    
                }
            }
        }
        catch (SQLException e)
        {
            throw new IOException(e);
        }
        
        return nm;
        
    }
    
    private <T> Iterable<T> toIterable(Iterator<T> it)
    {
        return () -> it;
    }
    
    @Override
    public void storeNewNewsletterPublicationResult(NewsletterPublicationResult res) throws IOException
    {
        File f;
        synchronized (nprDateFormat)  // SimpleDateFormat provides no internal synchronization
        {
            f = new File(nprFolder, nprDateFormat.format(new Date()) + ".json");
        }
        nprFolder.mkdirs();
        storeJsonSerializable(res, f);
    }
    
    private void storeJsonSerializable(JsonSerializable serializable, File file) throws IOException
    {
        try (JsonOutput out = new JsonOutput(Json.getOutputStream(new BufferedOutputStream(new FileOutputStream(file)), getZNews().config.getEnableJSONPrettyPrinting())))
        {
            out.write(serializable);
        }
    }
    
    private <T extends JsonSerializable> T queryJsonSerializable(File file, Supplier<T> ifNotExistsGet, Class<T> clazz) throws IOException
    {
        if (!file.exists())
            return ifNotExistsGet.get();
        try (JsonInput<T> in = new JsonInput<>(Json.getInputStream(new BufferedInputStream(new FileInputStream(file))), clazz))
        {
            return in.read();
        }
    }
    
    private void storeSerializable(Serializable serializable, File file) throws IOException
    {
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
        {
            out.writeObject(serializable);
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Serializable> T querySerializable(File file, Supplier<T> ifNotExistsGet) throws IOException
    {
        if (!file.exists())
            return ifNotExistsGet.get();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))))
        {
            return (T) in.readObject();
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException("Corrupted file: " + file.getAbsolutePath(), e);
        }
    }
    
    @Override
    @SneakyThrows
    public void doClose()
    {
        connection.close();
    }
    
}
