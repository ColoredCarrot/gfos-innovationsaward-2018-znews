package com.coloredcarrot.loggingapi;

import java.lang.ref.SoftReference;
import java.util.Date;

public final class LogRecord
{
    
    private final LogRecordOrigin origin;
    private final Object          message;
    private final long            millis;
    private final Level           level;
    private final Throwable       associatedThrowable;
    
    private transient SoftReference<Date> dateCreated;
    
    public LogRecord(LogRecordOrigin origin, Object message, Level level, Throwable associatedThrowable)
    {
        this(origin, message, System.currentTimeMillis(), level, associatedThrowable);
    }
    
    public LogRecord(LogRecordOrigin origin, Object message, long millis, Level level, Throwable associatedThrowable)
    {
        this.origin = origin;
        this.message = message;
        this.millis = millis;
        this.level = level;
        this.associatedThrowable = associatedThrowable;
    }
    
    public LogRecordOrigin getOrigin()
    {
        return origin;
    }
    
    public Object getMessage()
    {
        return message;
    }
    
    public long getCreated()
    {
        return millis;
    }
    
    public Level getLevel()
    {
        return level;
    }
    
    public Throwable getAssociatedThrowable()
    {
        return associatedThrowable;
    }
    
    public synchronized Date getDateCreated()
    {
        Date result;
        if (dateCreated == null || dateCreated.get() == null)
            dateCreated = new SoftReference<>(result = new Date(millis));
        else
            result = dateCreated.get();
        return result;
    }
    
    public enum Level
    {
        DEV,
        DEBUG,
        OUT,
        WARN,
        ERR,
        FATAL;
    
        public boolean shouldLog(Level level)
        {
            return level.ordinal() >= ordinal();
        }
        
        java.util.logging.Level getLevel()
        {
            switch (this)
            {
            case DEV:
                return java.util.logging.Level.FINEST;
            case DEBUG:
                return java.util.logging.Level.FINER;
            case OUT:
                return java.util.logging.Level.INFO;
            case WARN:
                return java.util.logging.Level.WARNING;
            case ERR:
                return java.util.logging.Level.SEVERE;
            case FATAL:
                return java.util.logging.Level.SEVERE;
            default:
                throw new AssertionError();
            }
        }
    }
    
}
