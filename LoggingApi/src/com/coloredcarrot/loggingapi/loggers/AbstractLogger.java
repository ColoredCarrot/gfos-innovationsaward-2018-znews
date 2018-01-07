package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.LogRecordOrigin;

import java.util.function.Function;

public abstract class AbstractLogger implements Logger
{
    
    protected LogRecord createRecord(String m, Throwable ex, LogRecord.Type type)
    {
        return new LogRecord(new LogRecordOrigin(getOriginStackTraceElement(), Thread.currentThread().getName(), Thread.currentThread().getId()), m, type, ex);
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
    public void log(String m, LogRecord.Type type)
    {
        log(m, null, type);
    }
    
    @Override
    public void log(String m, Throwable ex, LogRecord.Type type)
    {
        log(createRecord(m, ex, type));
    }
    
    @Override
    public void dev(String m)
    {
        log(m, LogRecord.Type.DEV);
    }
    
    @Override
    public void debug(String m)
    {
        log(m, LogRecord.Type.DEBUG);
    }
    
    @Override
    public void out(String m)
    {
        log(m, LogRecord.Type.OUT);
    }
    
    @Override
    public void warn(String m)
    {
        log(m, LogRecord.Type.WARN);
    }
    
    @Override
    public void warn(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Type.WARN);
    }
    
    @Override
    public void err(String m)
    {
        log(m, LogRecord.Type.ERR);
    }
    
    @Override
    public void err(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Type.ERR);
    }
    
    @Override
    public void fatal(String m)
    {
        log(m, LogRecord.Type.FATAL);
    }
    
    @Override
    public void fatal(String m, Throwable ex)
    {
        log(m, ex, LogRecord.Type.FATAL);
    }
    
    @Override
    public Logger withInterceptMessages(Function<? super String, ?> messageProcessor)
    {
        return Loggers.interceptMessages(this, messageProcessor);
    }
    
    @Override
    public Logger withCopyInto(Logger other)
    {
        return Loggers.combine(this, other);
    }
    
}
