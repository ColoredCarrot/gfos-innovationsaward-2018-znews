package de.znews.server.util;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.ExecutionException;

@ThreadSafe
public interface Cache<K, V>
{

    V compute(K key, ThrowingFunction<? super K, ? extends V, ? extends Exception> mapperFunction) throws ExecutionException, InterruptedException;

    void purge();

}
