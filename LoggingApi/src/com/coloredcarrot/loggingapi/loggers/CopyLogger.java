package com.coloredcarrot.loggingapi.loggers;

import com.coloredcarrot.loggingapi.LogRecord;

import java.util.Arrays;

public class CopyLogger extends DelegatingLogger
{
    
    private final Logger[] otherTargets;
    
    public CopyLogger(Logger... targets)
    {
        super(targets.length == 0 ? null : targets[0]);
        this.otherTargets = targets.length < 2 ? targets : Arrays.copyOfRange(targets, 1, targets.length);
    }
    
    @Override
    public void log(LogRecord record)
    {
        LogRecord processedRecord = process(record);
        if (target != null)
            target.log(processedRecord);
        for (Logger otherTarget : otherTargets)
            otherTarget.log(processedRecord);
    }
    
}
