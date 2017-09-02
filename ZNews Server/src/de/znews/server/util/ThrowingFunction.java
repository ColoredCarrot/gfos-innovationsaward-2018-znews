package de.znews.server.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable>
{
    
    R apply(T t) throws E;
    
    static <T, E extends RuntimeException> ThrowingFunction<T, T, E> identity()
    {
        return t -> t;
    }
    
    @NotNull
    @Contract(pure = true)
    static <T, R, E extends RuntimeException> ThrowingFunction<T, R, E> of(Function<T, R> function)
    {
        return function::apply;
    }
    
    static <T, R, E extends RuntimeException> ThrowingFunction<T, R, E> of(Runnable run, R result)
    {
        return t -> { run.run(); return result; };
    }
    
}
