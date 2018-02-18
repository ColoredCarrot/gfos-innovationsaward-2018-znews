package de.znews.server.resources;

import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import de.znews.server.ZNews;
import de.znews.server.resources.exception.HttpException;

public class EditSubscriptionDataResource extends JSONResource
{
    
    public EditSubscriptionDataResource(ZNews znews)
    {
        super(znews, "edit_subscription_data");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
    
        ctx.getStringParam("email");
        // TODO: Get data for email
    
        return JsonObject.createBuilder()
                         .add("success", true)
                         .build();
    
    }
    
}
