package de.znews.server.stat;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import com.coloredcarrot.jsonapi.reflect.JsonSerializer;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Immutable
@ThreadSafe
public class NewsletterPublicationResult implements JsonSerializable
{
    
    private final Set<String>                     successes;
    private final Map<String, MessagingException> failures;
    
    public NewsletterPublicationResult(Set<String> successes, Map<String, MessagingException> failures)
    {
        this.successes = Collections.unmodifiableSet(new HashSet<>(successes));
        this.failures = Collections.unmodifiableMap(new HashMap<>(failures));
    }
    
    public Set<String> getSuccesses()
    {
        return successes;
    }
    
    public Map<String, MessagingException> getFailures()
    {
        return failures;
    }
    
    public int getNumSuccesses()
    {
        return successes.size();
    }
    
    public int getNumFailures()
    {
        return failures.size();
    }
    
    public int getNumTotal()
    {
        return getNumSuccesses() + getNumFailures();
    }
    
    public double getSuccessRate()
    {
        return getNumTotal() == 0 ? 0 : getNumSuccesses() / getNumTotal();
    }
    
    public double getFailureRate()
    {
        return getNumTotal() == 0 ? 0 : getNumFailures() / getNumTotal();
    }
    
    @JsonSerializer
    JsonNode serialize()
    {
        JsonObject.Builder res = JsonObject.createBuilder();
        res.add("numSuccesses", getNumSuccesses());
        res.add("numFailures", getNumFailures());
        res.add("successes", successes);
        JsonObject.Builder failuresJson = JsonObject.createBuilder();
        failures.forEach((email, ex) ->
        {
            JsonArray.Builder err = JsonArray.createBuilder();
            Exception         exc = ex;
            do
            {
    
                Function<Throwable, JsonNode> shallowExSerializer = t ->
                {
                    JsonObject.Builder thisEx = JsonObject.createBuilder();
                    thisEx.add("type", t.getClass().getName());
                    thisEx.add("msg", t.getMessage());
                    thisEx.add("stackTrace", Arrays.stream(t.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining(" @ ")));
                    return thisEx.build();
                };
    
                JsonObject.Builder thisErr = JsonObject.createBuilder();
    
                thisErr.add("headException", shallowExSerializer.apply(exc));
                
                JsonArray.Builder causeChainJson = JsonArray.createBuilder();
                
                Throwable cause = exc.getCause();
                while (cause != null)
                {
                    causeChainJson.add(shallowExSerializer.apply(cause));
                    cause = cause.getCause();
                }
    
                thisErr.add("causeChain", causeChainJson.build());
    
                err.add(thisErr.build());
            }
            while (exc instanceof MessagingException && (exc = ((MessagingException) exc).getNextException()) != null);
            failuresJson.add(email, err.build());
        });
        res.add("failures", failuresJson.build());
        return res.build();
    }
    
    public static NewsletterPublicationResultBuilder builder()
    {
        return new NewsletterPublicationResultBuilder();
    }
    
    @ThreadSafe
    public static class NewsletterPublicationResultBuilder
    {
        private Set<String>                     successes = Collections.synchronizedSet(new HashSet<>());
        private Map<String, MessagingException> failures  = new ConcurrentHashMap<>();
        
        public NewsletterPublicationResultBuilder successes(Set<String> successes)
        {
            this.successes = successes;
            return this;
        }
        
        public NewsletterPublicationResultBuilder failures(Map<String, MessagingException> failures)
        {
            this.failures = failures;
            return this;
        }
        
        public boolean addSuccess(String email)
        {
            return successes.add(email);
        }
        
        public MessagingException addFailure(String email, MessagingException ex)
        {
            return failures.put(email, ex);
        }
        
        public NewsletterPublicationResult build()
        {
            return new NewsletterPublicationResult(successes, failures);
        }
    }
}
