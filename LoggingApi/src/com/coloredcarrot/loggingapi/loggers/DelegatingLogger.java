package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public abstract class DelegatingLogger extends AbstractLogger
{
    
    protected Logger target;
    
    public DelegatingLogger(Logger target)
    {
        this.target = target;
    }
    
    public LogRecord process(LogRecord record)
    {
        return record;
    }
    
    @Override
    public void log(LogRecord record)
    {
        if (target != null)
            target.log(process(record));
    }
    
    public Logger getTarget()
    {
        return target;
    }
    
    public void setTarget(Logger target)
    {
        this.target = target;
    }
    
    @Override
    public void shutdown()
    {
        if (target != null)
            target.shutdown();
    }
    
}
