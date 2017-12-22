package de.znews.server.util;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface CachedValue<T>
{
    
    T get();
    
}
