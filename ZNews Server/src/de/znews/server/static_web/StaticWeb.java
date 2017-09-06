package de.znews.server.static_web;

import de.znews.server.resources.RequestResponse;
import de.znews.server.util.Cache;
import de.znews.server.util.SoftLRUCache;
import de.znews.server.util.SynchronousCache;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@ThreadSafe
public class StaticWeb
{
    
    private final File                  root;
    @Getter
    private final Config                config;
    private final Cache<String, byte[]> cache;
    
    private final ConcurrentMap<File, FutureTask<byte[]>> loadingFiles = new ConcurrentHashMap<>();
    
    public StaticWeb(File root, Config config)
    {
        this.root = root;
        this.config = config;
        
        cache = config.isEnableCaching() ? new SoftLRUCache<>(config.getCacheSize()) : new SynchronousCache<>();
    }
    
    public void purgeCache()
    {
        cache.purge();
    }
    
    public RequestResponse getResponse(String path) throws InterruptedException
    {
        
        File file = getFile(path);
        
        if (file.isFile())
            return new RequestResponse(/* Chrome complains if we don't set the MIME type manually for Stylesheets */ file.getName().endsWith(".css") ? "text/css; charset=UTF-8" : null, get(path));
        else if (getFile(config.err404Path).isFile())
            return new RequestResponse(HttpResponseStatus.NOT_FOUND, get(config.err404Path));
        else
            return new RequestResponse(HttpResponseStatus.NOT_FOUND, "404 Not Found".getBytes(StandardCharsets.UTF_8));
        
    }
    
    public String getString(String path)
    {
        return new String(get(path));
    }
    
    public byte[] get(String path)
    {
        try
        {
            return cache.compute(path, this::loadFromFile);
        }
        catch (ExecutionException e)
        {
            System.err.println("Failed to load document from file");
            e.getCause().printStackTrace();
            return "Error".getBytes(StandardCharsets.UTF_8);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return get(path);
        }
    }
    
    private byte[] loadFromFile(String path) throws ExecutionException, InterruptedException
    {
        
        File file = new File(root, path);
    
        byte[] finalResult = loadingFiles.computeIfAbsent(file, f ->
        {
            FutureTask<byte[]> ft = new FutureTask<>(() ->
            {
            
                byte[] result;
                try (InputStream in = new BufferedInputStream(new FileInputStream(file));
                     ByteArrayOutputStream out = new ByteArrayOutputStream())
                {
                    int read;
                    while ((read = in.read()) != -1)
                        out.write(read);
                    result = out.toByteArray();
                }
            
                return result;
            
            });
            ft.run();
            return ft;
        }).get();
    
        loadingFiles.remove(file);
        
        return finalResult;
        
    }
    
    private File getFile(String path)
    {
        return new File(root, path);
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config
    {
        private boolean enableCaching = true;
        private String  err404Path    = "error/404notfound.html";
        private int     cacheSize     = 32;
    }
    
}
