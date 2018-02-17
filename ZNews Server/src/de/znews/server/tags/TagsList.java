package de.znews.server.tags;

import de.znews.server.Log;
import de.znews.server.ZNews;
import de.znews.server.util.Str;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class TagsList
{
    
    private static final String  DELIMITER = "\n";
    private static final Charset CHARSET   = StandardCharsets.UTF_8;
    
    private final ZNews       znews;
    private       Set<String> tags;
    
    public synchronized Set<String> getTags()
    {
        if (tags != null)
            return tags;
        File   f  = znews.staticWeb.getFile("list_tags.txt");
        byte[] bytes;
        try
        {
            bytes = Files.readAllBytes(f.toPath());
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
        return tags = Collections.synchronizedSet(new HashSet<>(Arrays.asList(new Str(bytes, CHARSET).splitString(DELIMITER.toCharArray(), -1))));  // Use Str for efficiency because String#split always uses a regex
    }
    
    public synchronized void addTag(String tag)
    {
        getTags().add(tag);
    }
    
    public void save() throws IOException
    {
        byte[] bytes = String.join(DELIMITER, this.tags.toArray(new String[0]))
                             .getBytes(CHARSET);
        File f = znews.staticWeb.getFile("list_tags.txt");
        Files.write(f.toPath(), bytes);
    }
    
}
