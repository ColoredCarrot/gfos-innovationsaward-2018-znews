package com.coloredcarrot.loggingapi.loggers;

import java.io.Writer;
import java.util.function.Function;

public interface Loggers
{
    
    static Logger trash()
    {
        return new CopyLogger();
    }
    
    static Logger to(Writer writer)
    {
        return new WriterLogger(writer);
    }
    
    static Logger interceptMessages(Logger targetLogger, Function<? super String, ?> messageProcessor)
    {
        return new MessageTransformingLoggerAdapter(targetLogger, messageProcessor);
    }
    
    static Logger combine(Logger... targets)
    {
        return new CopyLogger(targets);
    }
    
}
