package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public interface Logger
{
    
    void log(LogRecord record);
    
    void log(Object m, LogRecord.Level level);
    
    void log(Object m, Throwable ex, LogRecord.Level level);
    
    void dev(Object m);
    
    void debug(Object m);
    
    void out(Object m);
    
    void warn(Object m);
    
    void warn(Object m, Throwable ex);
    
    void err(Object m);
    
    void err(Object m, Throwable ex);
    
    void fatal(Object m);
    
    void fatal(Object m, Throwable ex);
    
    void shutdown();
    
    static LoggerBuilder builder()
    {
        return Loggers.builder();
    }

}
