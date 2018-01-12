package com.coloredcarrot.loggingapi.loggers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class FilesGzipLogger extends ToLinesLogger
{
    
    private static final DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private final    File    dir;
    private final    boolean autoFlush;
    private volatile Writer  writer;
    
    public FilesGzipLogger(File dir, boolean autoFlush)
    {
        this.dir = dir;
        this.autoFlush = autoFlush;
    }
    
    public FilesGzipLogger(String lineDelimiter, File dir, boolean autoFlush)
    {
        super(lineDelimiter);
        this.dir = dir;
        this.autoFlush = autoFlush;
    }
    
    @Override
    protected synchronized void write(String s)
    {
        openWriter();
        try
        {
            writer.write(s);
            if (autoFlush)
                writer.flush();
        }
        catch (IOException e)
        {
            closeWriter();
            throw new UncheckedIOException(e);
        }
    }
    
    private synchronized void closeWriter()
    {
        try
        {
            writer.flush();
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
            finally
            {
                gzipAndClearLatest();
            }
        }
    }
    
    private void gzipAndClearLatest()
    {
        File latest = new File(dir, "latest.log");
        if (!latest.exists())
            return;
        try
        {
            Files.copy(latest.toPath(), new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, getNewFileName())))));
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
    
    private synchronized void openWriter()
    {
        if (writer == null)
        {
            dir.mkdirs();
            try
            {
                writer = new OutputStreamWriter(new FileOutputStream(new File(dir, "latest.log")));
            }
            catch (FileNotFoundException e)
            {
                throw new UncheckedIOException(e);
            }
        }
    }
    
    private synchronized String getNewFileName()
    {
        dir.mkdirs();
        
        String   fileName      = fileNameDateFormat.format(new Date());
        String[] existingNames = dir.list((dir, name) -> name.endsWith(".log.gzip") && name.startsWith(fileName));
        int      appendNumber  = existingNames != null ? existingNames.length : 0;
        
        return appendNumber != 0 ? fileName + appendNumber : fileName;
    }
    
}
