package de.znews.server.resources.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Http400BadRequestException extends HttpException
{
	
	private static final long serialVersionUID = 5259316835075877009L;
	
	public Http400BadRequestException()
	{
		this("400 Bad Request");
	}
	
	public Http400BadRequestException(String msg)
	{
		super(HttpResponseStatus.BAD_REQUEST, msg);
	}
	
}
