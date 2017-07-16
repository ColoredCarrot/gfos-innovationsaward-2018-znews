package de.znews.server.resources.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Http403ForbiddenException extends HttpException
{
	
	private static final long serialVersionUID = -2189600602468729333L;
	
	public Http403ForbiddenException()
	{
		this("403 Forbidden");
	}
	
	public Http403ForbiddenException(String msg)
	{
		super(HttpResponseStatus.FORBIDDEN, msg);
	}
	
}
