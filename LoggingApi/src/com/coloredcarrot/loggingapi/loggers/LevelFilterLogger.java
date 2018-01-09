package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public class LevelFilterLogger extends FilterLogger
{
    
    private LogRecord.Level min;
    
    public LevelFilterLogger(Logger target, LogRecord.Level min)
    {
        super(target);
        this.min = min;
    }
    
    public LogRecord.Level getMin()
    {
        return min;
    }
    
    public void setMin(LogRecord.Level min)
    {
        this.min = min;
    }
    
    @Override
    protected boolean filter(LogRecord record)
    {
        return min.shouldLog(record.getLevel());
    }
    
}
