package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.HttpException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GetNewslettersResource extends JSONResource
{
    
    public GetNewslettersResource(ZNews znews)
    {
        super(znews, "api/get");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        int amount = ctx.getIntParam("amount", 5);
        
        Iterable<Newsletter> latestNewsletters = znews.newsletterManager.getLatestNewsletters(amount);
        
        // Include ids only if logged in and `includenid` parameter is specified
        List<JsonNode> dataAsList = (ctx.hasParam("includenid") ? dataWithIds(latestNewsletters, ctx) : dataWithoutIds(latestNewsletters))
                .collect(Collectors.toList());
        
        JsonArray.Builder data = JsonArray.createBuilder();
        
        dataAsList.forEach(data::add);
        
        return JsonObject.createBuilder().add("success", true).add("data", data.build()).build();
        
    }
    
    private Stream<JsonNode> dataWithIds(Iterable<Newsletter> newsletters, RequestContext ctx) throws HttpException
    {
        znews.authenticator.requireAuthentication(ctx);
        return StreamSupport.stream(newsletters.spliterator(), false)
                            .map(n -> JsonObject.createBuilder()
                                                .add("title", n.getTitle())
                                                .add("text", n.getText())
                                                .add("nid", n.getId())
                                                .build());
    }
    
    private Stream<JsonNode> dataWithoutIds(Iterable<Newsletter> newsletters)
    {
        return StreamSupport.stream(newsletters.spliterator(), false)
                            .map(n -> JsonObject.createBuilder()
                                                .add("title", n.getTitle())
                                                .add("text", n.getText())
                                                .build());
    }
    
}
