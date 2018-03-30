package de.znews.server.util;
import net.jcip.annotations.ThreadSafe;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

@ThreadSafe
public class SoftLRUCache<K, V> implements Cache<K, V>
{

    private static final int   DEFAULT_CACHE_SIZE  = 32;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final Map<K, RunnableFuture<SoftReference<V>>> map;

    /**
     * Constructs a new Cache with the default cache size
     * ({@value DEFAULT_CACHE_SIZE}) and default load factor
     * ({@value DEFAULT_LOAD_FACTOR}).
     */
    public SoftLRUCache()
    {
        this(DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructs a new Cache with the specified cache size
     * and the default load factor ({@value DEFAULT_LOAD_FACTOR}).
     *
     * @param cacheSize The maximum cache size
     */
    public SoftLRUCache(int cacheSize)
    {
        this(cacheSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new Cache with the specified cache size
     * and load factor.
     *
     * @param cacheSize  The cache size
     * @param loadFactor The load factor
     */
    public SoftLRUCache(int cacheSize, float loadFactor)
    {
        map = Collections.synchronizedMap(new LinkedHashMap<K, RunnableFuture<SoftReference<V>>>(cacheSize, loadFactor, true)
        {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, RunnableFuture<SoftReference<V>>> eldest)
            {
                return size() > cacheSize;
            }
        });
    }

    @Override
    public V compute(K key, ThrowingFunction<? super K, ? extends V, ? extends Exception> mapperFunction) throws ExecutionException, InterruptedException
    {
        V result;
        do result = computeRef(key, mapperFunction).get();
        while (result == null);
        return result;
    }

    SoftReference<V> computeRef(K key, ThrowingFunction<? super K, ? extends V, ? extends Exception> mapperFunction) throws ExecutionException, InterruptedException
    {

        RunnableFuture<SoftReference<V>> f = map.get(key);

        if (f == null && map.putIfAbsent(key, f = new FutureTask<>(() -> new SoftReference<V>(mapperFunction.apply(key)))) == null)
            f.run();

        return f.get();

    }

    @Override
    public void purge()
    {
        map.clear();
    }
    
/*@Nullable
    public synchronized V get(K key) throws ExecutionException, InterruptedException
    {
        Future<SoftReference<V>> f = map.get(key);
        if (f == null)
            return null;
        SoftReference<V> ref = f.get();
        return ref != null ? ref.get() : null;
    }
    
    public synchronized V getOrDefault(K key, V defaultValue)
    {
        V result = get(key);
        if (result != null)
            return result;
        put(key, defaultValue);
        return defaultValue;
    }
    
    public V getOrGetDefault(K key, Function<? super K, ? extends V> other)
    {
        synchronized (this)
        {
            V result = get(key);
            if (result != null)
                return result;
        }
        // Do not synchronize while computing value; it might take a long time
        V result = other.apply(key);
        synchronized (this)
        {
            // The value for key might have been
            oldmap.computeIfAbsent(key, k -> new SoftReference<V>(result));
        }
        return result;
    }
    
    public void put(K key, V value)
    {
        oldmap.put(key, new SoftReference<>(value));
    }*/

}
