package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LoggerBuilder extends AbstractLogger
{
    
    private DelegatingLogger first;
    private DelegatingLogger last;
    
    public LoggerBuilder format(String format, DateFormat dateFormat)
    {
        newPhase(new StringFormatLogger(null, format, dateFormat));
        return this;
    }
    
    public LoggerBuilder format(Object[][] f)
    {
        Map<LogRecord.Level, String>     formats     = new HashMap<>();
        Map<LogRecord.Level, DateFormat> dateFormats = new HashMap<>();
        for (Object[] e : f)
        {
            LogRecord.Level level = (LogRecord.Level) e[0];
            if (e[1] instanceof String)
            {
                formats.put(level, (String) e[1]);
                if (e.length >= 3 && e[2] instanceof DateFormat)
                    dateFormats.put(level, (DateFormat) e[2]);
            }
            else if (e[1] instanceof DateFormat)
                dateFormats.put(level, (DateFormat) e[1]);
        }
        newPhase(new StringFormatLogger(null, formats, dateFormats));
        return this;
    }
    
    public LoggerBuilder filter(LogRecord.Level minLevel)
    {
        newPhase(new LevelFilterLogger(null, minLevel));
        return this;
    }
    
    public LoggerBuilder process(Function<LogRecord, LogRecord> processor)
    {
        newPhase(new TransformingLogger(null, processor));
        return this;
    }
    
    public LoggerBuilder processMessage(Function<Object, ?> messageProcessor)
    {
        process(record -> new LogRecord(record.getOrigin(), String.valueOf(messageProcessor.apply(record.getMessage())), record.getCreated(), record.getLevel(), record.getAssociatedThrowable()));
        //newPhase(new MessageTransformingLoggerAdapter(null, messageProcessor));
        return this;
    }
    
    public LoggerBuilder copyInto(Logger dump)
    {
        newPhase(new CopyLogger(null, dump));
        return this;
    }
    
    public Logger saveInto(File file, Charset cs) throws FileNotFoundException
    {
        return saveInto(new OutputStreamWriter(new FileOutputStream(file), cs));
    }
    
    public Logger saveInto(PrintStream printStream)
    {
        return saveInto(new PrintWriter(printStream));
    }
    
    public Logger saveInto(Writer writer)
    {
        return saveInto(new WriterLogger(writer, true));
    }
    
    public Logger saveInto(Logger phase)
    {
        return build(phase);
    }
    
    protected void newPhase(DelegatingLogger phase)
    {
        if (first == null)
            first = last = phase;
        else
            last.target = last = phase;
    }
    
    public Logger build(Logger finalPhase)
    {
        if (first == null)
            return finalPhase;
        last.setTarget(finalPhase);
        return first;
    }
    
    @Override
    public void log(LogRecord record)
    {
        if (first != null)
            first.log(record);
    }
    
}
