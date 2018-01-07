package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.util.function.Function;

public interface Logger
{
    
    void log(LogRecord record);
    
    void log(String m, LogRecord.Type type);
    
    void log(String m, Throwable ex, LogRecord.Type type);
    
    void dev(String m);
    
    void debug(String m);
    
    void out(String m);
    
    void warn(String m);
    
    void warn(String m, Throwable ex);
    
    void err(String m);
    
    void err(String m, Throwable ex);
    
    void fatal(String m);
    
    void fatal(String m, Throwable ex);
    
    Logger withInterceptMessages(Function<? super String, ?> messageProcessor);
    
    Logger withCopyInto(Logger other);

}
