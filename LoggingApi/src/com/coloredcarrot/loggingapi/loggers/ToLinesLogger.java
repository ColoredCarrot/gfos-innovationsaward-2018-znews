package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringJoiner;

public abstract class ToLinesLogger extends AbstractLogger
{
    
    private final String lineDelimiter;
    
    public ToLinesLogger()
    {
        this("\n");
    }
    
    public ToLinesLogger(String lineDelimiter)
    {
        this.lineDelimiter = lineDelimiter;
    }
    
    protected abstract void write(String s);
    
    protected String toString(LogRecord record)
    {
        StringJoiner lines = new StringJoiner(lineDelimiter);
        lines.add(record.getMessage());
        if (record.getAssociatedThrowable() != null)
        {
            Throwable    ex = record.getAssociatedThrowable();
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw, true)
            {
                @Override
                public void println()
                {
                    write(lineDelimiter);
                }
            });
            lines.add(sw.toString());
        }
        return lines.toString();
    }
    
    @Override
    public void log(LogRecord record)
    {
        write(toString(record));
    }
    
}
