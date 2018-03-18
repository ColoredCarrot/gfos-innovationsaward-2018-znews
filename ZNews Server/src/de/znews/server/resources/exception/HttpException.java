package de.znews.server.resources.exception;

import de.znews.server.resources.RequestResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

public class HttpException extends Exception
{
	
	private static final long serialVersionUID = -1420826622131162648L;
	
	@Getter
	private final HttpResponseStatus status;
	
	public HttpException(HttpResponseStatus status, String msg)
	{
		super(msg);
		this.status = status;
	}
	
	public RequestResponse toResponse()
	{
		String m = getMessage();
		if (m == null)
		    m = "Not Found";
		return new RequestResponse(getStatus(), m.getBytes(StandardCharsets.UTF_8));
	}
	
}
