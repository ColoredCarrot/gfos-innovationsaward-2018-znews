package de.znews.server.util;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

public class SoftCachedValue<T> implements CachedValue<T>
{
    
    private final Supplier<? extends T> gen;
    private volatile SoftReference<T> ref;
    
    public SoftCachedValue(Supplier<? extends T> generator)
    {
        this.gen = generator;
    }
    
    @Override
    public T get()
    {
        
        // TODO: There's still the problem gen might be invoked multiple times in quick succession
        // This may be fixable using Semaphores
        
        T v;
        
        synchronized (this)
        {
            v = ref == null ? null : ref.get();
        }
        
        if (v != null)
            return v;
    
        v = gen.get();
    
        synchronized (this)
        {
            T rv = ref == null ? null : ref.get();
            if (rv != null)
                return rv;
            ref = new SoftReference<>(v);
        }
        
        return v;
        
    }
    
}
