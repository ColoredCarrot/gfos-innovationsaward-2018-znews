package de.znews.server;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.loggers.Logger;

public class Log
{
    
    private static Logger logger;
    
    public static Logger getLogger()
    {
        return logger;
    }
    
    public static void setLogger(Logger logger)
    {
        Log.logger = logger;
    }
    
    public static void log(LogRecord record)
    {
        logger.log(record);
    }
    
    public static void log(String m, LogRecord.Level level)
    {
        logger.log(m, level);
    }
    
    public static void log(String m, Throwable ex, LogRecord.Level level)
    {
        logger.log(m, ex, level);
    }
    
    public static void dev(String m)
    {
        logger.dev(m);
    }
    
    public static void debug(String m)
    {
        logger.debug(m);
    }
    
    public static void out(String m)
    {
        logger.out(m);
    }
    
    public static void warn(String m)
    {
        logger.warn(m);
    }
    
    public static void warn(String m, Throwable ex)
    {
        logger.warn(m, ex);
    }
    
    public static void err(String m)
    {
        logger.err(m);
    }
    
    public static void err(String m, Throwable ex)
    {
        logger.err(m, ex);
    }
    
    public static void fatal(String m)
    {
        logger.fatal(m);
    }
    
    public static void fatal(String m, Throwable ex)
    {
        logger.fatal(m, ex);
    }
    
    public static void shutdown()
    {
        logger.shutdown();
    }
    
}
