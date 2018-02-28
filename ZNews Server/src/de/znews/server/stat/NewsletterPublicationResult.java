package de.znews.server.stat;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.mail.MessagingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Immutable
@ThreadSafe
public class NewsletterPublicationResult
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
        return getNumSuccesses() / getNumTotal();
    }
    
    public double getFailureRate()
    {
        return getNumFailures() / getNumTotal();
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
