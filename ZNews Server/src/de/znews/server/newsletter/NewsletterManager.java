package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsletterManager implements Serializable, JsonSerializable
{
	
	private static final long serialVersionUID = 592773864822928724L;
	
	// Ordered latest newsletter first
	private List<Newsletter> newsletters = new ArrayList<>();
    private transient Map<String, Newsletter> nidToNewsletter = new HashMap<>();
	
	public void addNewsletter(Newsletter newsletter)
	{
		newsletters.add(0, newsletter);
	}
    
    public Newsletter getNewsletter(String nid) throws IllegalArgumentException
    {
        return nidToNewsletter.computeIfAbsent(nid, _nid -> newsletters.stream().filter(n -> n.getId().equals(nid)).findAny().orElseThrow(() -> new IllegalArgumentException("Newsletter is non-existent")));
    }
	
    public Iterable<Newsletter> getLatestNewsletters(int amount)
    {
        return amount >= newsletters.size() ? newsletters : newsletters.subList(0, amount);
    }
    
    @JsonDeserializer
    public static NewsletterManager deserializeJson(JsonObject json)
    {
        NewsletterManager newsletterManager = new NewsletterManager();
        newsletterManager.newsletters.addAll(json.getArray("newsletters").builder().getAll(Newsletter.class));
        for (Newsletter newsletter : newsletterManager.newsletters)
            newsletterManager.nidToNewsletter.put(newsletter.getId(), newsletter);
        return newsletterManager;
    }
    
}
