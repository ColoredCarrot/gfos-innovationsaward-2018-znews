package com.coloredcarrot.loggingapi;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.logging.Level;

public final class LogRecord
{
    
    private final LogRecordOrigin origin;
    private final String          message;
    private final long            millis;
    private final Type            type;
    private final Throwable       associatedThrowable;
    
    private transient SoftReference<Date> dateCreated;
    
    public LogRecord(LogRecordOrigin origin, String message, Type type, Throwable associatedThrowable)
    {
        this(origin, message, System.currentTimeMillis(), type, associatedThrowable);
    }
    
    public LogRecord(LogRecordOrigin origin, String message, long millis, Type type, Throwable associatedThrowable)
    {
        this.origin = origin;
        this.message = message;
        this.millis = millis;
        this.type = type;
        this.associatedThrowable = associatedThrowable;
    }
    
    public LogRecordOrigin getOrigin()
    {
        return origin;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public long getCreated()
    {
        return millis;
    }
    
    public Type getType()
    {
        return type;
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
    
    public enum Type
    {
        DEV,
        DEBUG,
        OUT,
        WARN,
        ERR,
        FATAL;
        
        Level getLevel()
        {
            switch (this)
            {
            case DEV:
                return Level.FINEST;
            case DEBUG:
                return Level.FINER;
            case OUT:
                return Level.INFO;
            case WARN:
                return Level.WARNING;
            case ERR:
                return Level.SEVERE;
            case FATAL:
                return Level.SEVERE;
            default:
                throw new AssertionError();
            }
        }
    }
    
}
