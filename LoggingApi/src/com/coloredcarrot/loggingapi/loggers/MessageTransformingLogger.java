package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public abstract class MessageTransformingLogger extends DelegatingLogger
{
    
    public MessageTransformingLogger(Logger target)
    {
        super(target);
    }
    
    protected abstract Object processMessage(Object m, LogRecord theRecord);
    
    @Override
    public LogRecord process(LogRecord record)
    {
        return new LogRecord(record.getOrigin(), processMessage(record.getMessage(), record), record.getCreated(), record.getLevel(), record.getAssociatedThrowable());
    }
    
}
