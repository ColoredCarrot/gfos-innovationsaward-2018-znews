package de.znews.server.tags;

import de.znews.server.Log;
import de.znews.server.ZNews;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class TagsList
{
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    
    private final ZNews       znews;
    private       Set<String> tags;
    
    public synchronized Set<String> getTags()
    {
        if (tags != null)
            return tags;
        File f = znews.staticWeb.getFile("list_tags.txt");
        if (!f.exists())
            return tags = Collections.synchronizedSet(new HashSet<>());
        try
        {
            return tags = Collections.synchronizedSet(new HashSet<>(Files.readAllLines(f.toPath(), CHARSET)));
        }
        catch (FileNotFoundException e)
        {
            return tags = Collections.synchronizedSet(new HashSet<>());
        }
        catch (IOException e)
        {
            Log.warn("Cannot read tags list from " + f.getAbsolutePath());
            return new HashSet<>();
        }
    }
    
    public synchronized void addTag(String tag)
    {
        getTags().add(tag);
    }
    
    public void save() throws IOException
    {
        if (tags == null)
            return;
        File f = znews.staticWeb.getFile("list_tags.txt");
        Files.write(f.toPath(), tags);
    }
    
}
