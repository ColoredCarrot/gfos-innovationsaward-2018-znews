package de.znews.server.lib;

import de.znews.server.Log;
import de.znews.server.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarExtractionUtil
{
    
    public static long extractDirectory(String pathInJar, Path targetFolder) throws IOException
    {
        
        Path path = pathInJar(pathInJar);
        
        if (path == null)
        {
            Log.warn("Could not extract directory " + pathInJar + " from JAR file to " + targetFolder + ": Not running from JAR file");
            return 0L;
        }
    
        AtomicLong size = new AtomicLong();
        copy(path, targetFolder, size);
        return size.get();
        
    }
    
    public static void copy(Path source, Path target, AtomicLong size) throws IOException
    {
        if (Files.isDirectory(source))
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(source))
            {
                for (Path child : ds)
                {
                    copy(child, target.resolve(child.getFileName().toString()), size);
                    size.addAndGet(Files.size(child));
                }
            }
        else
        {
            Files.createDirectories(target.getParent());
            Files.copy(source, target);
        }
    }
    
    /*public static Path pathInJar(String pathInJar) throws IOException
    {
        FileSystem fs = fileSystem(pathInJar);
        return fs != null ? fs.getPath(pathInJar) : null;
    }*/
    
    public static Path pathInJar(String pathInJar) throws IOException
    {
        
        URI jarLoc;
        try
        {
            jarLoc = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
        
        Log.debug("Detected code source location: " + jarLoc.getPath());
        
        try
        {
            new JarFile(new File(jarLoc.getPath()));
            return FileSystems.newFileSystem(new File(jarLoc.getPath()).toPath(), null)
                              .getPath(pathInJar);
        }
        catch (FileNotFoundException e)
        {
            // Probably running from an IDE / not a JAR
            return Paths.get(jarLoc);
        }
        
    }
    
    public static void extractZipped(String pathInJar, File targetFolder) throws IOException
    {
        
        URL resource = Main.class.getResource(pathInJar);
        
        if (resource == null)
            throw new ResourceNotFoundException(pathInJar);
        
        try (ZipInputStream zis = new ZipInputStream(resource.openStream()))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                
                System.out.println(entry.getName());
                
            }
        }
        
    }
    
    public static class ResourceNotFoundException extends IOException
    {
        private static final long serialVersionUID = -8277180044759530459L;
        
        public ResourceNotFoundException()
        {
        }
        
        public ResourceNotFoundException(String message)
        {
            super(message);
        }
        
        public ResourceNotFoundException(String message, Throwable cause)
        {
            super(message, cause);
        }
        
        public ResourceNotFoundException(Throwable cause)
        {
            super(cause);
        }
    }
    
}
