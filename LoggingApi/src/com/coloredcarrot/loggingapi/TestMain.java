package com.coloredcarrot.loggingapi;

import com.coloredcarrot.loggingapi.loggers.Logger;
import com.coloredcarrot.loggingapi.loggers.Loggers;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class TestMain
{
    
    public static void main(String[] args) throws IOException
    {
        /*Logger logger = Logger.builder()
                              .filter(LogRecord.Level.OUT)
                              .delegateToOtherThreads()
                              .format(new Object[][] {
                                      { LogRecord.Level.DEV, "[{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}", new SimpleDateFormat("HH:mm:ss:SSS") },
                                      { LogRecord.Level.DEBUG, "[{date}] [{threadName}:{threadId}/{level}] {msg}" },
                                      { LogRecord.Level.FATAL, "FATAL [{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}" }
                              })
                              .copyInto(System.out)
                              .saveInto(new FilesGzipLogger(new File("the_mf_logs/"), true));*/
        //.build();
    
        Properties props  = new Properties();
        props.load(new StringReader("log.filter=out\n" +
                "log.format.dev=[{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}\n" +
                "log.format.dev.date-format=HH:mm:ss:SSS\n" +
                "log.format.debug=[{date}] [{threadName}:{threadId}/{level}] {msg}\n" +
                "log.format.fatal=FATAL [{date}] [{threadName}:{threadId}/{level} @ {methodName}/{typeSimpleName}/{fileName}:{line}] {msg}\n" +
                "log.out.files-gzip.dir=the_log/\n" +
                "log.out.files-gzip.auto-flush=true\n" +
                "log.separate-thread=true"));
        Logger     logger = Loggers.build(props, System.out);
        
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
