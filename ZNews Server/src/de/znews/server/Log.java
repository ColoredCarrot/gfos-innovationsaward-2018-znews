package de.znews.server;

import com.coloredcarrot.loggingapi.LogRecord;
import com.coloredcarrot.loggingapi.loggers.Logger;

import java.util.function.Supplier;

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
    
    public static void log(Object m, LogRecord.Level level)
    {
        logger.log(m, level);
    }
    
    public static void log(Object m, Throwable ex, LogRecord.Level level)
    {
        logger.log(m, ex, level);
    }
    
    public static void dev(Object m)
    {
        logger.dev(m);
    }
    
    public static void debug(Object m)
    {
        logger.debug(m);
    }
    
    public static void out(Object m)
    {
        logger.out(m);
    }
    
    public static void warn(Object m)
    {
        logger.warn(m);
    }
    
    public static void warn(Object m, Throwable ex)
    {
        logger.warn(m, ex);
    }
    
    public static void err(Object m)
    {
        logger.err(m);
    }
    
    public static void err(Object m, Throwable ex)
    {
        logger.err(m, ex);
    }
    
    public static void fatal(Object m)
    {
        logger.fatal(m);
    }
    
    public static void fatal(Object m, Throwable ex)
    {
        logger.fatal(m, ex);
    }
    
    public static void log(Supplier<?> m, LogRecord.Level level)
    {
        logger.log(m, level);
    }
    
    public static void log(Supplier<?> m, Throwable ex, LogRecord.Level level)
    {
        logger.log(m, ex, level);
    }
    
    public static void dev(Supplier<?> m)
    {
        logger.dev(m);
    }
    
    public static void debug(Supplier<?> m)
    {
        logger.debug(m);
    }
    
    public static void out(Supplier<?> m)
    {
        logger.out(m);
    }
    
    public static void warn(Supplier<?> m)
    {
        logger.warn(m);
    }
    
    public static void warn(Supplier<?> m, Throwable ex)
    {
        logger.warn(m, ex);
    }
    
    public static void err(Supplier<?> m)
    {
        logger.err(m);
    }
    
    public static void err(Supplier<?> m, Throwable ex)
    {
        logger.err(m, ex);
    }
    
    public static void fatal(Supplier<?> m)
    {
        logger.fatal(m);
    }
    
    public static void fatal(Supplier<?> m, Throwable ex)
    {
        logger.fatal(m, ex);
    }
    
    public static void shutdown()
    {
        logger.shutdown();
    }
    
}
