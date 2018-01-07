package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class TransformingLogger extends DelegatingLogger
{
    
    private final Function<LogRecord, LogRecord> transformer;
    
    public TransformingLogger(Logger target, Collection<Function<LogRecord, LogRecord>> transformers)
    {
        super(target);
        this.transformer = transformers.stream().reduce(Function.identity(), Function::andThen);
    }
    
    @SafeVarargs
    public TransformingLogger(Logger target, Function<LogRecord, LogRecord>... transformers)
    {
        super(target);
        this.transformer = Arrays.stream(transformers).reduce(Function.identity(), Function::andThen);
    }
    
    @Override
    public LogRecord process(LogRecord record)
    {
        return transformer.apply(record);
    }
    
}
