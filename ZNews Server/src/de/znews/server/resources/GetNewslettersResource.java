package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.resources.exception.HttpException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class GetNewslettersResource extends JSONResource
{
    
    private static final List<String> TRUE_STRINGS = Collections.unmodifiableList(Arrays.asList("true", "1", "ok", "yes"));
    
    public GetNewslettersResource(ZNews znews)
    {
        super(znews, "admin/api/get");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        boolean includeNonPublished = ctx.hasParam("include-non-published") && TRUE_STRINGS.contains(ctx.getStringParam("include-non-published").toLowerCase(Locale.ENGLISH));
        
        if (includeNonPublished)
            znews.sessionManager.requireHttpAuthentication(ctx);
        
        int amount = ctx.getIntParam("amount", 5);
        
        Stream<Newsletter> newsletterStream = znews.newsletterManager.getLatestNewsletters();
        
        if (!includeNonPublished)
            newsletterStream = newsletterStream.filter(Newsletter::isPublished);
        
        newsletterStream = newsletterStream.limit(amount);
        
        Stream<JsonObject.Builder> stream = newsletterStream.map(n -> JsonObject.createBuilder()
                                                                                .add("title", n.getTitle())
                                                                                .add("the_delta", n.getContent())
                                                                                .add("published", n.isPublished())
                                                                                .add("nid", n.getId()));
        
        if (!includeNonPublished)
            stream = stream.map(n -> n.remove("published"));
        
        JsonArray.Builder data = JsonArray.createBuilder();
        
        stream.map(JsonObject.Builder::build)
              .forEach(data::add);
        
        return JsonObject.createBuilder().add("success", true).add("data", data.build()).build();
        
    }
    
}
