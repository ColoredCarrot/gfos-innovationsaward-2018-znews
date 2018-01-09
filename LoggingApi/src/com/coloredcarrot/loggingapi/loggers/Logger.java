package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

public interface Logger
{
    
    void log(LogRecord record);
    
    void log(String m, LogRecord.Level level);
    
    void log(String m, Throwable ex, LogRecord.Level level);
    
    void dev(String m);
    
    void debug(String m);
    
    void out(String m);
    
    void warn(String m);
    
    void warn(String m, Throwable ex);
    
    void err(String m);
    
    void err(String m, Throwable ex);
    
    void fatal(String m);
    
    void fatal(String m, Throwable ex);
    
    static LoggerBuilder builder()
    {
        return Loggers.builder();
    }

}
