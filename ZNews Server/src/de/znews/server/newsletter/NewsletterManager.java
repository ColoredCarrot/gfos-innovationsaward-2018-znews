package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsletterManager implements Serializable, JsonSerializable
{
	
	private static final long serialVersionUID = 592773864822928724L;
	
	// Ordered latest newsletter first
	private List<Newsletter> newsletters = new ArrayList<>();
	
	public void addNewsletter(Newsletter newsletter)
	{
		newsletters.add(0, newsletter);
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
        return newsletterManager;
    }
    
}
