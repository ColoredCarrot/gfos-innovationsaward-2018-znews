package de.znews.server.resources;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.parsing.JsonException;
import de.znews.server.ZNews;
import de.znews.server.newsletter.Registration;
import de.znews.server.resources.exception.Http400BadRequestException;
import de.znews.server.resources.exception.HttpException;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.StringReader;
import java.util.HashSet;
import java.util.stream.Collectors;

public class EditSubscriptionDataResource extends JSONResource
{
    
    public EditSubscriptionDataResource(ZNews znews)
    {
        super(znews, "edit_subscription_data");
    }
    
    @Override
    public JsonNode handleJsonRequest(RequestContext ctx) throws HttpException
    {
        
        String email = ctx.getStringParam("email");
        
        Registration reg = znews.registrationList.getRegistration(email);
        
        if (reg == null)
            throw new HttpException(HttpResponseStatus.NOT_FOUND, email);
        
        // Currently, we don't support other data types but "application/x-www-form-urlencoded";
        // we must use some other data serialization format in the parameter values
        if (ctx.hasPostParam("update_subscribed_tags"))
        {
            
            JsonArray newTags;
            try
            {
                newTags = (JsonArray) Json.getInputStream(new StringReader(ctx.getStringPostParam("update_subscribed_tags")))
                                          .next();
            }
            catch (JsonException | ClassCastException e)
            {
                throw new Http400BadRequestException("Malformed or invalid JSON (" + e.getMessage() + ")");
            }
            
            // Update subscribed-to tags.
            // All JSON contents (of the array) are converted to String.
            // We may want to check whether they are any other type and then
            //  throw an Http400BadRequestException.
            reg.setSubscribedTags(new HashSet<>(
                    newTags.getContents().stream()
                           .map(JsonNode::stringValue)
                           .collect(Collectors.toSet())
            ));
            
            // Don't cancel method execution so the client has a chance to
            // review the results (NYI).
            
        }
        
        // Just return all subscribed-to tags
        return JsonArray.createBuilder()
                        .addAll(reg.getAllSubscribedTags(znews))
                        .build();
        
    }
    
}
