package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;

import java.util.Arrays;

public class ListTagsResource extends JSONResource
{
    
    public ListTagsResource(ZNews znews)
    {
        super(znews, "list_tags");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
    
        int limit = ctx.getIntParam("limit", -1);
        
        if (limit <= 0)
            limit = -1;
    
        JsonArray.Builder json = JsonArray.createBuilder();
    
        String[] array = znews.tagsList.getTags().toArray(new String[limit == -1 ? 0 : limit]);
        if (limit != -1 && array.length > limit)
            array = Arrays.copyOf(array, limit);
    
        for (String tag : array)
            json.add(tag);
    
        return json.build();
        
    }
    
}
