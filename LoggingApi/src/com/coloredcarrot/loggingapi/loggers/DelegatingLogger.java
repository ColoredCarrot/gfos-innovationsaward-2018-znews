package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public abstract class DelegatingLogger extends AbstractLogger
{
    
    protected final Logger target;
    
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
        target.log(process(record));
    }
    
}
