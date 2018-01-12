package com.coloredcarrot.loggingapi;

import com.coloredcarrot.loggingapi.loggers.FilesGzipLogger;
import com.coloredcarrot.loggingapi.loggers.Logger;

import java.io.File;
import java.text.SimpleDateFormat;

public class TestMain
{
    
    public static void main(String[] args)
    {
        Logger logger = Logger.builder()
                              .filter(LogRecord.Level.OUT)
                              .delegateToOtherThreads()
                              .format(new Object[][] {
                                      { LogRecord.Level.DEV, "[{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}", new SimpleDateFormat("HH:mm:ss:SSS") },
                                      { LogRecord.Level.DEBUG, "[{date}] [{threadName}:{threadId}/{level}] {msg}" },
                                      { LogRecord.Level.FATAL, "FATAL [{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}" }
                              })
                              .copyInto(System.out)
                              .saveInto(new FilesGzipLogger(new File("the_mf_logs/"), true));
        //.build();
        
        logger.dev("my dev");
        logger.debug("my debug");
        logger.out("my out");
        logger.err("my err", new RuntimeException("Hello, ", new NullPointerException("world!")));
        logger.fatal("my fatal");
        
        new Thread(() ->
        {
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
        
    }
    
}
