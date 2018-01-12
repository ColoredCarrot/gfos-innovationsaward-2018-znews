package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.io.File;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public interface Loggers
{
    
    static LoggerBuilder builder()
    {
        return new LoggerBuilder();
    }
    
    static Logger build(Properties props, PrintStream copyInto)
    {
        LoggerBuilder b = builder()
                .filter(LogRecord.Level.valueOf(props.getProperty("log.filter", "out").toUpperCase(Locale.ENGLISH)));
    
        if (Boolean.parseBoolean(props.getProperty("log.separate-thread", "true")))
            b.delegateToOtherThreads();
        
        Map<LogRecord.Level, Map.Entry<String, DateFormat>> formats = new HashMap<>();
        for (Map.Entry<Object, Object> e : props.entrySet())
        {
            String key = String.valueOf(e.getKey());
            if (key.startsWith("log.format."))
            {
                String          levelName = key.substring("log.format.".length(), key.endsWith(".date-format") ? key.length() - ".date-format".length() : key.length());
                LogRecord.Level level     = LogRecord.Level.valueOf(levelName.toUpperCase(Locale.ENGLISH));
                if (key.endsWith(".date-format"))
                {
                    DateFormat newDateFormat = new SimpleDateFormat(String.valueOf(e.getValue()));
                    formats.compute(level, (l, old) -> old == null ? new AbstractMap.SimpleImmutableEntry<>(null, newDateFormat) : new AbstractMap.SimpleImmutableEntry<>(old.getKey(), newDateFormat));
                }
                else
                {
                    String newFormat = String.valueOf(e.getValue());
                    formats.compute(level, (l, old) -> old == null ? new AbstractMap.SimpleImmutableEntry<>(newFormat, null) : new AbstractMap.SimpleImmutableEntry<>(newFormat, old.getValue()));
                }
            }
        }
        
        List<Object[]> list = new ArrayList<>();
        for (Map.Entry<LogRecord.Level, Map.Entry<String, DateFormat>> e : formats.entrySet())
            list.add(new Object[] { e.getKey(), e.getValue().getKey(), e.getValue().getValue() });
        Object[][] formatsArray = list.toArray(new Object[0][0]);
    
        b.format(formatsArray);
        
        if (copyInto != null)
            b.copyInto(copyInto);
    
        // Right now, only logging to a directory using gzip is supported
        File logDir = new File(props.getProperty("log.out.files-gzip.dir", "log/"));
        return b.saveInto(new FilesGzipLogger(logDir, Boolean.parseBoolean(props.getProperty("log.out.files-gzip.auto-flush", "true"))));
        
    }
    
    static Logger trash()
    {
        return new CopyLogger();
    }
    
    static Logger to(Writer writer)
    {
        return new WriterLogger(writer);
    }
    
    static DelegatingLogger interceptMessages(Logger targetLogger, Function<Object, ?> messageProcessor)
    {
        return new MessageTransformingLoggerAdapter(targetLogger, messageProcessor);
    }
    
    static DelegatingLogger process(Function<LogRecord, LogRecord> processor, Logger target)
    {
        return new DelegatingLogger(target)
        {
            @Override
            public LogRecord process(LogRecord record)
            {
                return processor.apply(record);
            }
        };
    }
    
    static DelegatingLogger combine(Logger... targets)
    {
        return new CopyLogger(targets);
    }
    
}
