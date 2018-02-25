package de.znews.server.resources;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.uri.URIFragment;

import java.nio.charset.StandardCharsets;

public abstract class JSONResource extends Resource
{
    
    public JSONResource(ZNews znews, String params)
    {
        super(znews, URIFragment.fromURI(params));
    }
    
    @Override
    public RequestResponse handleRequest(RequestContext ctx) throws HttpException
    {
        JsonNode jsonResponse = handleJsonRequest(ctx);
        return new RequestResponse("text/json", Json.toString(jsonResponse).getBytes(StandardCharsets.UTF_8));
    }
    
    protected abstract JsonNode handleJsonRequest(RequestContext ctx) throws HttpException;
    
}
