package de.znews.server.resources.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Http404NotFoundException extends HttpException
{
	
	private static final long serialVersionUID = 1298098473874089880L;
	
	public Http404NotFoundException()
	{
		this("Not Found");
	}
	
	public Http404NotFoundException(String msg)
	{
		super(HttpResponseStatus.NOT_FOUND, msg);
	}
	
}
