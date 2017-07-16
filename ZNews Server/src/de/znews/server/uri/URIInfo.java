package de.znews.server.uri;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Deprecated
@Getter
public class URIInfo
{
	
	private String protocol;
	private String protocolExt = "://";
	private int port;
	@Setter
	private boolean trailingSlash = true;
	private URIFragment firstFragment;
	private URIFragment lastFragment;
	
	public URIInfo(String protocol, int port, URIFragment firstFragment)
	{
		this.protocol = protocol;
		setPort(port);
		this.firstFragment = firstFragment;
		
		// Find last fragment
		URIFragment fragment = firstFragment;
		//noinspection ConstantConditions
		while (fragment.hasNext())
			fragment = fragment.getNext();
		lastFragment = fragment;
	}
	
	@Deprecated
	public URIInfo(String s)
	{
		
		char[] c = s.toCharArray();
		int    i = 0;
		
		StringBuilder protocolBuilder = new StringBuilder();
		
		char protocolChar;
		while ((protocolChar = c[i++]) != ' ')
			protocolBuilder.append(protocolChar);
		
		throw new UnsupportedOperationException();
		
	}
	
	public void setPort(int port)
	{
		if (port < 0)
			this.port = -1;
		else
			this.port = port;
	}
	
	public void clearPort()
	{
		port = -1;
	}
	
	
	@Override
	public String toString()
	{
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(protocol).append(protocolExt);
		
		if (port != -1)
			sb.append(':').append(port);
		
		if (firstFragment == null)
		{
			if (trailingSlash)
				sb.append('/');
		}
		else
		{
			sb.append(firstFragment.toString());
			if (trailingSlash)
				sb.append('/');
		}
		
		return sb.toString();
		
	}
	
	
	public URI toURI()
	{
		return URI.create(toString());
	}
	
}
