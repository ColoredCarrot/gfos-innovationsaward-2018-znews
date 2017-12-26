package de.znews.server.newsletter;

import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import de.znews.server.Main;
import de.znews.server.emai_reg.NewNewsletterEmail;

import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class NewsletterManager implements Serializable, JsonSerializable
{
    
    // TODO: Make Thread-Safe
    
    private static final long serialVersionUID = 592773864822928724L;
    
    // Ordered latest newsletter first
    private           List<Newsletter>        newsletters     = new ArrayList<>();
    private transient Map<String, Newsletter> nidToNewsletter = new HashMap<>();
    
    public synchronized void addNewsletter(Newsletter newsletter)
    {
        newsletters.add(0, newsletter);
    }
    
    public synchronized Newsletter getNewsletter(String nid) throws IllegalArgumentException
    {
        return nidToNewsletter.computeIfAbsent(nid, _nid -> newsletters.stream().filter(n -> n.getId().equals(nid)).findAny().orElseThrow(() -> new IllegalArgumentException("Newsletter is non-existent")));
    }
    
    public synchronized Stream<Newsletter> getLatestNewsletters()
    {
        return newsletters.stream();
    }
    
    public synchronized void doDeleteNewsletter(String nid)
    {
        nidToNewsletter.remove(nid);
        ArrayList<Newsletter> newslettersCopy = new ArrayList<>(this.newsletters);
        for (int i = 0; i < newslettersCopy.size(); i++)
            if (newslettersCopy.get(i).getId().equals(nid))
            {
                newsletters.remove(i);
                return;
            }
        throw new IllegalArgumentException("Newsletter is non-existent");
    }
    
    public synchronized void doPublishNewsletter(String nid, UUID publisher)
    {
        int        index = getIndexOfNewsletter(nid);
        Newsletter n     = newsletters.remove(index);
        n.setPublished(true);
        n.setDatePublished(new Date());
        n.setPublisher(publisher);
        newsletters.add(0, n);
        
        // Asynchronously send email to all registered subscribers
        // TODO: Use a thread pool
        new Thread(() ->
        {
            MutableDataSet mkToHtmlOpts = new MutableDataSet();
            mkToHtmlOpts.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create()));
            String html = HtmlRenderer.builder(mkToHtmlOpts).build()
                                      .render(Parser.builder(mkToHtmlOpts).build().parse(n.getText()));
            
            Main.getZnews().registrationList.forEach(reg ->
            {
                NewNewsletterEmail email = new NewNewsletterEmail(Main.getZnews());
                email.setTitle(n.getTitle());
                email.setWithHtml(html);
                email.setWithoutHtml(n.getText());
                email.setRegisteredEmail(reg.getEmail());
                email.setNid(n.getId());
                try
                {
                    email.send(reg.getEmail());
                }
                catch (MessagingException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }).start();
    }
    
    private int getIndexOfNewsletter(String nid)
    {
        for (int i = 0; i < newsletters.size(); i++)
            if (newsletters.get(i).getId().equals(nid))
                return i;
        throw new IllegalArgumentException("Newsletter is non-existent");
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
