package de.znews.server.util;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface CachedValue<T>
{
    
    T get();
    
}
