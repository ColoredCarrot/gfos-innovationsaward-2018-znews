package de.znews.server.util;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.ExecutionException;

@ThreadSafe
public class SynchronousCache<K, V> implements Cache<K, V>
{
    
    @Override
    public V compute(K key, ThrowingFunction<? super K, ? extends V, ? extends Exception> mapperFunction) throws ExecutionException, InterruptedException
    {
        try
        {
            return mapperFunction.apply(key);
        }
        catch (Exception e)
        {
            throw new ExecutionException(e);
        }
    }
    
    @Override
    @Deprecated
    public void purge()
    {
    }
    
}
