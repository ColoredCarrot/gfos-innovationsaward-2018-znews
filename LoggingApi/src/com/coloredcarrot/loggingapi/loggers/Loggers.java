package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.io.Writer;
import java.util.function.Function;

public interface Loggers
{
    
    static LoggerBuilder builder()
    {
        return new LoggerBuilder();
    }
    
    static Logger trash()
    {
        return new CopyLogger();
    }
    
    static Logger to(Writer writer)
    {
        return new WriterLogger(writer);
    }
    
    static DelegatingLogger interceptMessages(Logger targetLogger, Function<Object, ?> messageProcessor)
    {
        return new MessageTransformingLoggerAdapter(targetLogger, messageProcessor);
    }
    
    static DelegatingLogger process(Function<LogRecord, LogRecord> processor, Logger target)
    {
        return new DelegatingLogger(target)
        {
            @Override
            public LogRecord process(LogRecord record)
            {
                return processor.apply(record);
            }
        };
    }
    
    static DelegatingLogger combine(Logger... targets)
    {
        return new CopyLogger(targets);
    }
    
}
