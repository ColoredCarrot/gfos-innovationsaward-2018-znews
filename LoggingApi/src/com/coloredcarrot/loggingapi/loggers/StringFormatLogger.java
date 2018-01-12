package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.LogRecordOrigin;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class StringFormatLogger extends MessageTransformingLogger
{
    
    private Map<LogRecord.Level, String>     formats;
    private Map<LogRecord.Level, DateFormat> dateFormats;
    
    public StringFormatLogger(Logger target, String format, DateFormat dateFormat)
    {
        super(target);
        this.formats = new HashMap<>();
        this.dateFormats = new HashMap<>();
        for (LogRecord.Level level : LogRecord.Level.values())
        {
            formats.put(level, format);
            dateFormats.put(level, dateFormat);
        }
    }
    
    public StringFormatLogger(Logger target, Map<LogRecord.Level, String> formats, Map<LogRecord.Level, DateFormat> dateFormats)
    {
        super(target);
        if (formats.isEmpty() || dateFormats.isEmpty())
            throw new IllegalArgumentException("formats or dateFormats empty");
        this.formats = new HashMap<>(formats);
        this.dateFormats = new HashMap<>(dateFormats);
        LogRecord.Level[] levels = LogRecord.Level.values();
        for (int i = 0; i < levels.length; i++)
        {
            int             finalI = i;
            LogRecord.Level level  = levels[i];
            this.formats.computeIfAbsent(level, finalI == 0
                                                ? l -> formats.values().iterator().next()
                                                : l -> this.formats.get(levels[finalI - 1]));
            this.dateFormats.computeIfAbsent(level, finalI == 0
                                                ? l -> dateFormats.values().iterator().next()
                                                : l -> this.dateFormats.get(levels[finalI - 1]));
        }
    }
    
    @Override
    protected String processMessage(Object m, LogRecord r)
    {
        assert m == r.getMessage();
        return format(r);
    }
    
    public String format(LogRecord r)
    {
        String          f  = formats.get(r.getLevel());
        DateFormat      df = dateFormats.get(r.getLevel());
        LogRecordOrigin o  = r.getOrigin();
        return f.replace("{threadName}", o.getThreadName())
                .replace("{threadId}", String.valueOf(o.getThreadId()))
                .replace("{typeName}", o.getTypeName())
                .replace("{typeSimpleName}", o.getTypeName().substring(o.getTypeName().lastIndexOf('.') + 1))
                .replace("{fileName}", o.getFileName())
                .replace("{methodName}", o.getMethodName())
                .replace("{line}", String.valueOf(o.getLineNumber()))
                .replace("{exMsg}", r.getAssociatedThrowable() == null || r.getAssociatedThrowable().getMessage() == null ? "" : r.getAssociatedThrowable().getMessage())
                .replace("{date}", df.format(r.getDateCreated()))
                .replace("{millis}", String.valueOf(r.getCreated()))
                .replace("{level}", r.getLevel().name())
                .replace("{msg}", String.valueOf(r.resolveMessage()));
    }
    
}
