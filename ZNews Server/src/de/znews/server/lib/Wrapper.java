package de.znews.server.lib;

public class Wrapper<T>
{
    
    private T value;
    
    public Wrapper()
    {
    }
    
    public Wrapper(T value)
    {
        this.value = value;
    }
    
    public T get()
    {
        return value;
    }
    
    public T set(T newValue)
    {
        T oldValue = value;
        value = newValue;
        return oldValue;
    }
    
}
