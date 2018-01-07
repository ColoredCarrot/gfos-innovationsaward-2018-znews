package com.coloredcarrot.loggingapi.loggers;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MessageTransformingLoggerAdapter extends MessageTransformingLogger
{
    
    private final Function<String, String> messageTransformer;
    
    public MessageTransformingLoggerAdapter(Logger target, Iterable<? extends Function<? super String, ?>> transformers)
    {
        this(target, StreamSupport.stream(transformers.spliterator(), false));
    }
    
    @SafeVarargs
    public MessageTransformingLoggerAdapter(Logger target, Function<? super String, ?>... transformers)
    {
        this(target, Arrays.stream(transformers));
    }
    
    public MessageTransformingLoggerAdapter(Logger target, Stream<? extends Function<? super String, ?>> transformersStream)
    {
        super(target);
        this.messageTransformer = transformersStream.map(f -> (Function<String, String>) s -> String.valueOf(f.apply(s)))
                                                    .reduce(Function::andThen)
                                                    .orElse(Function.identity());
    }
    
    @Override
    protected String processMessage(String m)
    {
        return messageTransformer.apply(m);
    }
}
