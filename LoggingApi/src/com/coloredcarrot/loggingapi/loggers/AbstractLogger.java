package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.LogRecordOrigin;

public abstract class AbstractLogger implements Logger
{
    
    protected LogRecord createRecord(String m, Throwable ex, LogRecord.Level level)
    {
        return new LogRecord(new LogRecordOrigin(getOriginStackTraceElement(), Thread.currentThread().getName(), Thread.currentThread().getId()), m, level, ex);
    }
    
    protected StackTraceElement getOriginStackTraceElement()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String p = Logger.class.getPackage().getName();
        for (int i = 1; i < stackTrace.length; i++)
        {
            if (stackTrace[i].getClassName().startsWith(p))
                continue;
            try
            {
                Class<?> c = Class.forName(stackTrace[i].getClassName());
                if (!Logger.class.isAssignableFrom(c))
                    return stackTrace[i];
            }
            catch (ClassNotFoundException e)
            {
                return stackTrace[i];
            }
        }
        return null;
    }
    
    @Override
    public void log(String m, LogRecord.Level level)
    {
        log(m, null, level);
    }
    
    @Override
    public void log(String m, Throwable ex, LogRecord.Level level)
    {
        log(createRecord(m, ex, level));
    }
    
    @Override
    public void dev(String m)
    {
        log(m, LogRecord.Level.DEV);
    }
    
    @Override
    public void debug(String m)
    {
        log(m, LogRecord.Level.DEBUG);
    }
    
    @Override
    public void out(String m)
    {
        log(m, LogRecord.Level.OUT);
    }
    
    @Override
    public void warn(String m)
    {
        log(m, LogRecord.Level.WARN);
    }
    
    @Override
    public void warn(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.WARN);
    }
    
    @Override
    public void err(String m)
    {
        log(m, LogRecord.Level.ERR);
    }
    
    @Override
    public void err(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.ERR);
    }
    
    @Override
    public void fatal(String m)
    {
        log(m, LogRecord.Level.FATAL);
    }
    
    @Override
    public void fatal(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.FATAL);
    }
    
    @Override
    public void shutdown()
    {
    }
    
}
