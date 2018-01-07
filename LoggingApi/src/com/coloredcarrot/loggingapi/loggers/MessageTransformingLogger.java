package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public abstract class MessageTransformingLogger extends DelegatingLogger
{
    
    public MessageTransformingLogger(Logger target)
    {
        super(target);
    }
    
    protected abstract String processMessage(String m);
    
    @Override
    public LogRecord process(LogRecord record)
    {
        return new LogRecord(record.getOrigin(), processMessage(record.getMessage()), record.getCreated(), record.getType(), record.getAssociatedThrowable());
    }
    
}
