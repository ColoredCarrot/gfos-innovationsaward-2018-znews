package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewThreadLogger extends DelegatingLogger
{
    
    public static ExecutorService DEFAULT_THREAD_POOL = Executors.newSingleThreadExecutor();
    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            if (DEFAULT_THREAD_POOL != null)
                DEFAULT_THREAD_POOL.shutdown();
        }));
    }
    
    private final ExecutorService threadPool;
    
    public NewThreadLogger(Logger target)
    {
        this(target, DEFAULT_THREAD_POOL);
    }
    
    public NewThreadLogger(Logger target, ExecutorService threadPool)
    {
        super(target);
        this.threadPool = threadPool;
    }
    
    @Override
    public void log(LogRecord record)
    {
        if (target != null)
            threadPool.execute(() ->
            {
                if (target != null)
                    target.log(record);
            });
    }
    
}
