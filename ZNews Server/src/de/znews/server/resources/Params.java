package de.znews.server.resources;

import io.netty.handler.codec.http.cookie.Cookie;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class Params implements Iterable<Param>
{
	
	public static Params fromCookies(Set<Cookie> cookies)
	{
		return new Params(cookies.stream().map(cookie -> new Param(cookie.name(), cookie.value())).toArray(Param[]::new));
	}
	
	private final Param[] indexedParams;
	private final Map<String, Param> params = new HashMap<>();
	
	public Params(Map<? extends String, ? extends String> params)
	{
		this(params.entrySet().stream().map(e -> new Param(e.getKey(), e.getValue())).toArray(Param[]::new));
	}
	
	public Params(Params... combine)
	{
		this(Arrays.stream(combine).flatMap(e -> Arrays.stream(e.indexedParams)).toArray(Param[]::new));
	}
	
	public Params(Param... params)
	{
		this.indexedParams = params;
		for (Param param : params)
			this.params.put(param.getKey(), param);
	}
	
	public Param getParam(String key)
	{
		return params.get(key);
	}
	
	public Param getParam(int index)
	{
		return indexedParams[index];
	}
	
	public String getParamStringValue(String key)
	{
		return hasParam(key) ? getParam(key).getValue() : null;
	}
	
	public boolean hasParam(String key)
	{
		return params.containsKey(key);
	}
	
	@Override
	public String toString()
	{
		StringJoiner sj = new StringJoiner(", ", "[", "]");
		for (Param param : indexedParams)
			sj.add(param.getKey() + '=' + param.getValue());
		return sj.toString();
	}
	
	@SneakyThrows
	public Params withURLDecodedValues()
	{
		Param[] params = new Param[indexedParams.length];
		for (int i = 0; i < indexedParams.length; i++)
		{
			Param param = indexedParams[i];
			params[i] = new Param(param.getKey(), URLDecoder.decode(param.getValue(), "UTF-8"));
		}
		return new Params(params);
	}
	
	@NotNull
	@Override
	public Iterator<Param> iterator()
	{
        return Spliterators.iterator(Arrays.spliterator(indexedParams));
    }
    
    @Override
    public void forEach(Consumer<? super Param> action)
    {
        for (Param indexedParam : indexedParams)
            action.accept(indexedParam);
    }
    
    @Override
    public Spliterator<Param> spliterator()
    {
        return Arrays.spliterator(indexedParams);
    }
    
}
