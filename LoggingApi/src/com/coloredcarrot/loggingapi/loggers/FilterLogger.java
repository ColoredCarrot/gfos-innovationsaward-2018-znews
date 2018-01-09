package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public class FilterLogger extends DelegatingLogger
{
    
    public FilterLogger(Logger target)
    {
        super(target);
    }
    
    @Override
    public void log(LogRecord record)
    {
        if (filter(record))
            super.log(record);
    }
    
    protected boolean filter(LogRecord record)
    {
        return true;
    }
    
}
