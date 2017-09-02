package de.znews.server.util;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

@Deprecated
@RequiredArgsConstructor
public class CharArraysReader extends Reader
{
    
    private final Supplier<? extends char[]> cbufSupplier;
    
    private char[] buffer;
    private int    bufferIndex;
    
    @Override
    public int read() throws IOException
    {
        if (bufferIndex == -1)
            return -1;
        if (++bufferIndex < buffer.length)
            return buffer[bufferIndex];
        readBuffer();
        return read();
    }
    
    @Override
    public int read(@Nonnull char[] cbuf, int off, int len) throws IOException
    {
        if (bufferIndex + 1 == buffer.length)
            readBuffer();
        if (bufferIndex == -1)
            return 0;
        
        int amountRead = Math.min(buffer.length - bufferIndex, len);
        
        System.arraycopy(buffer, bufferIndex, cbuf, off, amountRead);
        
        bufferIndex += amountRead;
        
        if (amountRead == len)
            return amountRead;
        
        readBuffer();
        return read(cbuf, off + amountRead, len - amountRead);
        
    }
    
    @Override
    public void close() throws IOException
    {
        buffer = null;
        bufferIndex = -1;
    }
    
    public char[] getBuffer()
    {
        return buffer;
    }
    
    private void readBuffer()
    {
        buffer = cbufSupplier.get();
        bufferIndex = buffer != null ? 0 : -1;
    }
    
}
