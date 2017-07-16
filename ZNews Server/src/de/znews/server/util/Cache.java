package de.znews.server.util;

import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<K, V>
{
	
	private static final int DEFAULT_CACHE_SIZE = 10;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private Map<K, SoftReference<V>> map;
	
	/**
	 * Constructs a new Cache with the default cache size
	 * ({@value DEFAULT_CACHE_SIZE}) and default load factor
	 * ({@value DEFAULT_LOAD_FACTOR}).
	 */
	public Cache()
	{
		this(DEFAULT_CACHE_SIZE);
	}
	
	/**
	 * Constructs a new Cache with the specified cache size
	 * and the default load factor ({@value DEFAULT_LOAD_FACTOR}).
	 * @param cacheSize The maximum cache size
	 */
	public Cache(int cacheSize)
	{
		this(cacheSize, DEFAULT_LOAD_FACTOR);
	}
	
	/**
	 * Constructs a new Cache with the specified cache size
	 * and load factor.
	 * @param cacheSize The cache size
	 * @param loadFactor The load factor
	 */
	public Cache(int cacheSize, float loadFactor)
	{
		map = new LinkedHashMap<K, SoftReference<V>>(cacheSize, loadFactor, true)
		{
			private static final long serialVersionUID = -7204770977566838627L;
			
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest)
			{
				return size() > cacheSize;
			}
		};
	}
	
	@Nullable
	public V get(K key)
	{
		SoftReference<V> vRef = map.get(key);
		return vRef != null ? vRef.get() : null;
	}
	
	public V getOrDefault(K key, V defaultValue)
	{
		V result = get(key);
		if (result != null)
			return result;
		put(key, defaultValue);
		return defaultValue;
	}
	
	public V getOrGetDefault(K key, Function<? super K, ? extends V> other)
	{
		V result = get(key);
		if (result != null)
			return result;
		result = other.apply(key);
		put(key, result);
		return result;
	}
	
	public void put(K key, V value)
	{
		map.put(key, new SoftReference<>(value));
	}
	
	public void purge()
	{
		map.clear();
	}
	
}
