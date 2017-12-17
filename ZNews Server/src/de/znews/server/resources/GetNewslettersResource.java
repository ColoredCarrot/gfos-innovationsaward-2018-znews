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
        
        boolean includenid          = ctx.hasParam("includenid") && TRUE_STRINGS.contains(ctx.getStringParam("includenid").toLowerCase(Locale.ENGLISH));
        boolean includeNonPublished = ctx.hasParam("include-non-published") && TRUE_STRINGS.contains(ctx.getStringParam("include-non-published").toLowerCase(Locale.ENGLISH));
        
        if (includenid || includeNonPublished)
            znews.authenticator.requireAuthentication(ctx);
        
        int amount = ctx.getIntParam("amount", 5);
        
        Stream<Newsletter> newsletterStream = znews.newsletterManager.getLatestNewsletters()
                                                                     .limit(amount);
        
        if (!includeNonPublished)
            newsletterStream = newsletterStream.filter(Newsletter::isPublished);
        
        Stream<JsonObject.Builder> stream = includenid ? newsletterStream.map(n -> JsonObject.createBuilder()
                                                                                             .add("title", n.getTitle())
                                                                                             .add("text", n.getText())
                                                                                             .add("published", n.isPublished())
                                                                                             .add("nid", n.getId()))
                                                       : newsletterStream.map(n -> JsonObject.createBuilder()
                                                                                             .add("title", n.getTitle())
                                                                                             .add("text", n.getText())
                                                                                             .add("published", n.isPublished()));
        
        if (!includeNonPublished)
            stream = stream.map(n -> n.remove("published"));
        
        JsonArray.Builder data = JsonArray.createBuilder();
        
        stream.map(JsonObject.Builder::build)
              .forEach(data::add);
        
        return JsonObject.createBuilder().add("success", true).add("data", data.build()).build();
        
    }
    
}
