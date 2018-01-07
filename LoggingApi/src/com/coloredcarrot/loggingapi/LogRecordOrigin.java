package com.coloredcarrot.loggingapi;

public final class LogRecordOrigin
{
    
    private final StackTraceElement stackTraceElement;
    private final String            threadName;
    private final long              threadId;
    
    public LogRecordOrigin(StackTraceElement stackTraceElement, String threadName, long threadId)
    {
        this.stackTraceElement = stackTraceElement;
        this.threadName = threadName;
        this.threadId = threadId;
    }
    
    public StackTraceElement getStackTraceElement()
    {
        return stackTraceElement;
    }
    
    public String getThreadName()
    {
        return threadName;
    }
    
    public long getThreadId()
    {
        return threadId;
    }
    
    public String getFileName()
    {
        return getStackTraceElement().getFileName();
    }
    
    public int getLineNumber()
    {
        return getStackTraceElement().getLineNumber();
    }
    
    public String getTypeName()
    {
        return getStackTraceElement().getClassName();
    }
    
    public Class<?> getType()
    {
        try
        {
            return Class.forName(getTypeName());
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    public String getMethodName()
    {
        return getStackTraceElement().getMethodName();
    }
    
}
