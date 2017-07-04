package de.znews.server.resources;

import lombok.Getter;
import lombok.Setter;
import org.reflections.Reflections;

public abstract class ClassScanner
{
	
	@Getter
	@Setter
	private Reflections reflections;
	
	public ClassScanner(String name)
	{
		this(new Reflections(name));
	}
	
	public ClassScanner(Reflections reflections)
	{
		this.reflections = reflections;
	}
	
	public abstract void scanClass(Class<?> theClass);
	
}
