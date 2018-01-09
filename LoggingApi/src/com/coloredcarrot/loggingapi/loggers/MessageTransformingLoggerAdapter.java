package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MessageTransformingLoggerAdapter extends MessageTransformingLogger
{
    
    private final Function<Object, ?> messageTransformer;
    
    public MessageTransformingLoggerAdapter(Logger target, Iterable<? extends Function<Object, ?>> transformers)
    {
        this(target, StreamSupport.stream(transformers.spliterator(), false));
    }
    
    @SafeVarargs
    public MessageTransformingLoggerAdapter(Logger target, Function<Object, ?>... transformers)
    {
        this(target, Arrays.stream(transformers));
    }
    
    public MessageTransformingLoggerAdapter(Logger target, Stream<? extends Function<Object, ?>> transformersStream)
    {
        super(target);
        this.messageTransformer = transformersStream.map(f -> (Function<Object, Object>) f)
                                                    .reduce(Function::andThen)
                                                    .orElse(x -> x);
    }
    
    @Override
    public Object processMessage(Object m, LogRecord theRecord)
    {
        return messageTransformer.apply(m);
    }
}
