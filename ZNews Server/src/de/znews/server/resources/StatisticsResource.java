package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Newsletter;
import de.znews.server.newsletter.Registration;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;

import java.util.Map;

public class StatisticsResource extends JSONResource
{
    
    public StatisticsResource(ZNews znews)
    {
        super(znews, "admin/api/statistics");
    }
    
    @Override
    protected JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        znews.sessionManager.requireHttpAuthentication(ctx);
        if (ctx.hasParam("nid"))
            return handlePublication(ctx, ctx.getStringParam("nid"));
        if (ctx.hasParam("email"))
            return handleEmail(ctx, ctx.getStringParam("email"));
        return handleOverview(ctx);
    }
    
    protected JsonNode handleOverview(RequestContext ctx) throws HttpException
    {
        // List all newsletters and emails
        return JsonObject.createBuilder()
                         .add("publications", znews.newsletterManager
                                 .getLatestNewsletters()
                                 .filter(Newsletter::isPublished)
                                 .map(n -> JsonObject.createBuilder()
                                                     .add("nid", n.getId())
                                                     .add("title", n.getTitle())
                                                     // TODO: views statistics
                                                     .build())
                                 .reduce(JsonArray.createBuilder(), JsonArray.Builder::add, (b1, b2) -> b1.addAll(b2.get().getContents()))
                                 .build())
                         .add("registrations", znews.registrationList
                                 .getAllRegistrations()
                                 .reduce(JsonObject.createBuilder(),
                                         (res, r) ->
                                                 res.add(r.getEmail(),
                                                         JsonObject.createBuilder()
                                                                   .add("dateRegistered", r.getDateRegistered())
                                                                   .build()),
                                         (b1, b2) ->
                                         {
                                             for (Map.Entry<String, JsonNode> e : b2.build().getMappings().entrySet())
                                                 b1 = b1.add(e.getKey(), e.getValue());
                                             return b1;
                                         })
                                 /*.map(r -> JsonObject.createBuilder()
                                                     .add("email")
                                                     .build())
                                 .reduce(JsonArray.createBuilder(), JsonArray.Builder::add, (b1, b2) -> b1.addAll(b2.get().getContents()))*/
                                 .build())
                         .build();
    }
    
    protected JsonNode handlePublication(RequestContext ctx, String nid) throws HttpException
    {
        Newsletter n = znews.newsletterManager.getNewsletter(nid);
        if (!n.isPublished())
            throw new Http400BadRequestException("newsletter is not published");
        return JsonObject.createBuilder()
                         .add("nid", n.getId())
                         .add("title", n.getTitle())
                         .add("tags", n.getTags())
                         .add("published", n.getDatePublished())
                         .add("publisher", n.getPublisherName(znews))
                         // TODO: Collect views statistics
                         .build();
    }
    
    protected JsonNode handleEmail(RequestContext ctx, String email) throws HttpException
    {
        Registration r = znews.registrationList.getRegistration(email);
        return JsonObject.createBuilder()
                         .add("email", r.getEmail())
                         .add("subscribed_tags", r.getAllSubscribedTags(znews))
                         .build();
    }
    
}
