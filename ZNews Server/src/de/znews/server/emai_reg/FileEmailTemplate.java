package de.znews.server.emai_reg;

import de.znews.server.Main;
import de.znews.server.ZNews;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public class FileEmailTemplate extends EmailTemplate
{
    
    private final ZNews  znews;
    private final String plaintextPathInJar, htmlPathInJar;
    private final File   file;
    private final String subject;
    private       String plaintext, html;
    
    public FileEmailTemplate(ZNews znews, String plaintextPathInJar, String htmlPathInJar, File file, String subject)
    {
        this.znews = znews;
        this.plaintextPathInJar = plaintextPathInJar;
        this.htmlPathInJar = htmlPathInJar;
        this.file = file;
        this.subject = subject;
    }
    
    private void loadIfNecessary()
    {
        if (plaintext == null && html == null)
            loadSilently();
    }
    
    private void loadSilently()
    {
        try
        {
            load();
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
    
    private synchronized void load() throws IOException
    {
        File file = this.file;
        if (file.exists())
            loadFromFile(file);
        else
            loadFromJar(file);
    }
    
    private void loadFromFile(File file) throws IOException
    {
        file = file.getAbsoluteFile();
        if (file.isFile())
            loadSingleFromDocument(file);
        else if (file.isDirectory())
        {
            File[] children = file.listFiles();
            assert children != null;
            for (File child : children)
                if (child.isFile())
                    loadSingleFromDocument(child);
                else
                    System.err.println("WARNING: Child of email template directory is not a file");
            if (plaintext == null && html == null)
                throw new IOException("Email template is missing plaintext AND html");
        }
        else
            throw new IOException("File is neither file nor directory");
    }
    
    private void loadSingleFromDocument(File file) throws IOException
    {
        if (file.getName().endsWith("html"))
        {
            html = String.join("\n", Files.readAllLines(file.toPath()));
        }
        else
        {
            plaintext = String.join("\n", Files.readAllLines(file.toPath()));
        }
    }
    
    private void loadFromJar(File file) throws IOException
    {
        
        if (plaintextPathInJar != null && htmlPathInJar != null)
        {
            file.mkdirs();
            try (InputStream in = Main.class.getResourceAsStream(plaintextPathInJar))
            {
                Files.copy(in, new File(file, plaintextPathInJar.substring(plaintextPathInJar.lastIndexOf('/') + 1)).toPath());
            }
            try (InputStream in = Main.class.getResourceAsStream(htmlPathInJar))
            {
                Files.copy(in, new File(file, htmlPathInJar.substring(htmlPathInJar.lastIndexOf('/') + 1)).toPath());
            }
        }
        else if (plaintextPathInJar != null)
        {
            file.getParentFile().mkdirs();
            try (InputStream in = Main.class.getResourceAsStream(plaintextPathInJar))
            {
                Files.copy(in, file.toPath());
            }
        }
        else if (htmlPathInJar != null)
        {
            file.getParentFile().mkdirs();
            try (InputStream in = Main.class.getResourceAsStream(htmlPathInJar))
            {
                Files.copy(in, file.toPath());
            }
        }
        
        loadFromFile(file);
        
    }
    
    @Override
    public synchronized String getSubject()
    {
        return subject;
    }
    
    @Override
    public synchronized String getPlaintext()
    {
        loadIfNecessary();
        return plaintext;
    }
    
    @Override
    public synchronized String getHtml()
    {
        loadIfNecessary();
        return html;
    }
    
}
