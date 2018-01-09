package com.coloredcarrot.loggingapi;

import com.coloredcarrot.loggingapi.loggers.Logger;

import java.text.SimpleDateFormat;

public class TestMain
{
    
    public static void main(String[] args)
    {
        Logger logger = Logger.builder()
                              //.filter(LogRecord.Level.OUT)
                              .format(new Object[][] {
                                      { LogRecord.Level.DEV, "[{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}", new SimpleDateFormat("HH:mm:ss:SSS") },
                                      { LogRecord.Level.DEBUG, "[{date}] [{threadName}:{threadId}/{level}] {msg}" },
                                      { LogRecord.Level.FATAL, "FATAL [{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}" }
                              })
                              .saveInto(System.out);
                              //.build();
    
        logger.dev("my dev");
        logger.debug("my debug");
        logger.out("my out");
        logger.err("my err", new RuntimeException("Hello, ", new NullPointerException("world!")));
        logger.fatal("my fatal");
        
    }
    
}
