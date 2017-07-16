package de.znews.server.static_web;

import de.znews.server.resources.RequestResponse;
import de.znews.server.util.Cache;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class StaticWeb
{
	
	private final File   root;
	@Getter
	private final Config config;
	private final Cache<String, byte[]> cache = new Cache<>();
	
	public void purgeCache()
	{
		cache.purge();
	}
	
	public RequestResponse getResponse(String path)
	{
		
		File file = getFile(path);
		
		if (file.isFile())
			return new RequestResponse(/* Chrome complains if we don't set the MIME type manually for Stylesheets */ file.getName().endsWith(".css") ? "text/css; charset=UTF-8" : null, load(path));
		else if (getFile(config.err404Path).isFile())
			return new RequestResponse(HttpResponseStatus.NOT_FOUND, load(config.err404Path));
		else
			return new RequestResponse(HttpResponseStatus.NOT_FOUND, "404 Not Found".getBytes(StandardCharsets.UTF_8));
		
	}
	
	private byte[] load(String path)
	{
		return config.enableCaching
		       ? cache.getOrGetDefault(path, this::loadFromFile)
		       : loadFromFile(path);
	}
	
	@SneakyThrows
	private byte[] loadFromFile(String path)
	{
		
		File file = new File(root, path);
		
		try (InputStream in = new BufferedInputStream(new FileInputStream(file));
		     ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			int read;
			while ((read = in.read()) != -1)
				out.write(read);
			return out.toByteArray();
		}
		
	}
	
	private File getFile(String path)
	{
		return new File(root, path);
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	public static class Config
	{
		private boolean enableCaching = true;
		private String  err404Path    = "error/404notfound.html";
		
		public Config()
		{
		}
	}
	
}
