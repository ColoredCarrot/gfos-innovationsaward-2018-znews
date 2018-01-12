package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.LogRecordOrigin;

import java.util.function.Supplier;

public abstract class AbstractLogger implements Logger
{
    
    protected LogRecord createRecord(Object m, Throwable ex, LogRecord.Level level)
    {
        return new LogRecord(new LogRecordOrigin(getOriginStackTraceElement(), Thread.currentThread().getName(), Thread.currentThread().getId()), m, level, ex);
    }
    
    protected StackTraceElement getOriginStackTraceElement()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String p = Logger.class.getPackage().getName();
        for (int i = 1; i < stackTrace.length; i++)
        {
            // Skip stack trace element if internal class or the class ends with "Log", in which case we assume a delegating logger
            if (stackTrace[i].getClassName().startsWith(p) || stackTrace[i].getClassName().endsWith("Log"))
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
    public void log(Object m, LogRecord.Level level)
    {
        log(m, null, level);
    }
    
    @Override
    public void log(Object m, Throwable ex, LogRecord.Level level)
    {
        log(createRecord(m, ex, level));
    }
    
    @Override
    public void dev(Object m)
    {
        log(m, LogRecord.Level.DEV);
    }
    
    @Override
    public void debug(Object m)
    {
        log(m, LogRecord.Level.DEBUG);
    }
    
    @Override
    public void out(Object m)
    {
        log(m, LogRecord.Level.OUT);
    }
    
    @Override
    public void warn(Object m)
    {
        log(m, LogRecord.Level.WARN);
    }
    
    @Override
    public void warn(Object m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.WARN);
    }
    
    @Override
    public void err(Object m)
    {
        log(m, LogRecord.Level.ERR);
    }
    
    @Override
    public void err(Object m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.ERR);
    }
    
    @Override
    public void fatal(Object m)
    {
        log(m, LogRecord.Level.FATAL);
    }
    
    @Override
    public void fatal(Object m, Throwable ex)
    {
        log(m, ex, LogRecord.Level.FATAL);
    }
    
    @Override
    public void log(Supplier<?> m, LogRecord.Level level)
    {
        log((Object) m, level);
    }
    
    @Override
    public void log(Supplier<?> m, Throwable ex, LogRecord.Level level)
    {
        log((Object) m, ex, level);
    }
    
    @Override
    public void dev(Supplier<?> m)
    {
        dev((Object) m);
    }
    
    @Override
    public void debug(Supplier<?> m)
    {
        debug((Object) m);
    }
    
    @Override
    public void out(Supplier<?> m)
    {
        out((Object) m);
    }
    
    @Override
    public void warn(Supplier<?> m)
    {
        warn((Object) m);
    }
    
    @Override
    public void warn(Supplier<?> m, Throwable ex)
    {
        warn((Object) m, ex);
    }
    
    @Override
    public void err(Supplier<?> m)
    {
        err((Object) m);
    }
    
    @Override
    public void err(Supplier<?> m, Throwable ex)
    {
        err((Object) m, ex);
    }
    
    @Override
    public void fatal(Supplier<?> m)
    {
        fatal((Object) m);
    }
    
    @Override
    public void fatal(Supplier<?> m, Throwable ex)
    {
        fatal((Object) m, ex);
    }
    
    @Override
    public void shutdown()
    {
    }
    
}
