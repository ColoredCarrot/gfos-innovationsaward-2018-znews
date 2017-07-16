package de.znews.server.resources;

import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Params
{
	
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
	
}
