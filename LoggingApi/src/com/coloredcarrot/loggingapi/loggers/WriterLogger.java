package com.coloredcarrot.loggingapi.loggers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class WriterLogger extends ToLinesLogger
{
    
    private static final Consumer<? super IOException> DEFAULT_EX_HANDLER = e -> { throw new UncheckedIOException(e); };
    
    private final Writer                        writer;
    private final boolean                       autoFlush;
    private final Consumer<? super IOException> exHandler;
    
    public WriterLogger(OutputStream outputStream)
    {
        this(new OutputStreamWriter(outputStream));
    }
    
    public WriterLogger(OutputStream outputStream, Charset cs)
    {
        this(new OutputStreamWriter(outputStream, cs));
    }
    
    public WriterLogger(OutputStream outputStream, boolean autoFlush)
    {
        this(new OutputStreamWriter(outputStream), autoFlush);
    }
    
    public WriterLogger(OutputStream outputStream, Charset cs, boolean autoFlush)
    {
        this(new OutputStreamWriter(outputStream, cs), autoFlush);
    }
    
    public WriterLogger(Writer writer)
    {
        this(writer, false);
    }
    
    public WriterLogger(Writer writer, boolean autoFlush)
    {
        this(writer, autoFlush, DEFAULT_EX_HANDLER);
    }
    
    public WriterLogger(Writer writer, boolean autoFlush, Consumer<? super IOException> exHandler)
    {
        this.writer = writer;
        this.autoFlush = autoFlush;
        this.exHandler = exHandler;
    }
    
    public WriterLogger(String lineDelimiter, Writer writer, boolean autoFlush, Consumer<? super IOException> exHandler)
    {
        super(lineDelimiter);
        this.writer = writer;
        this.autoFlush = autoFlush;
        this.exHandler = exHandler;
    }
    
    @Override
    protected void write(String s)
    {
        try
        {
            writer.write(s);
            if (autoFlush)
                writer.flush();
        }
        catch (IOException e)
        {
            exHandler.accept(e);
        }
    }
    
}
