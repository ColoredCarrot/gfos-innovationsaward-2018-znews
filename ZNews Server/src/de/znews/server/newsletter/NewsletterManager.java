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
import de.znews.server.Log;
import de.znews.server.Main;
import de.znews.server.emai_reg.NewNewsletterEmail;
import de.znews.server.stat.NewsletterPublicationResult;

import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class NewsletterManager implements Serializable, JsonSerializable
{
    
    // TODO: Make Thread-Safe
    
    private static final long serialVersionUID = 592773864822928724L;
    
    // Ordered latest newsletter first
    private           List<Newsletter>        newsletters     = new ArrayList<>();
    private transient Map<String, Newsletter> nidToNewsletter = new HashMap<>();
    
    public synchronized String getRandomNewsletterId()
    {
        String[] keyArray = nidToNewsletter.values().stream()
                                           .filter(Newsletter::isPublished)
                                           .map(Newsletter::getId)
                                           .toArray(String[]::new);
        return keyArray[ThreadLocalRandom.current().nextInt(keyArray.length)];
    }
    
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
        Log.debug(String.format("Publishing %s ...", nid));
        
        int        index = getIndexOfNewsletter(nid);
        Newsletter n     = newsletters.remove(index);
        n.setPublished(true);
        n.setDatePublished(new Date());
        n.setPublisher(publisher);
        newsletters.add(0, n);
        
        // Asynchronously send email to all registered subscribers
        Main.getZnews().server.getWorkerGroup().execute(() ->
        {
            
            NewsletterPublicationResult.NewsletterPublicationResultBuilder resBuilder = NewsletterPublicationResult.builder();
            
            MutableDataSet mkToHtmlOpts = new MutableDataSet();
            mkToHtmlOpts.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create()));
            String html = HtmlRenderer.builder(mkToHtmlOpts).build()
                                      .render(Parser.builder(mkToHtmlOpts).build().parse(n.getText()));
            
            RegistrationList registrationListCopy = Main.getZnews().registrationList;
            CountDownLatch   finishLatch          = new CountDownLatch(registrationListCopy.getNumRegistrations());
            
            registrationListCopy.forEach(reg ->
            {
                // Don't sent if tags are set (for legacy reasons) and if the registration does not subscribe to at least one tag
                if (n.getTags() != null && Arrays.stream(n.getTags()).noneMatch(reg::isSubscribedToTag))
                    return;
                NewNewsletterEmail email = new NewNewsletterEmail(Main.getZnews());
                email.setTitle(n.getTitle());
                email.setWithHtml(html);
                email.setWithoutHtml(n.getText());
                email.setRegisteredEmail(reg.getEmail());
                email.setNid(n.getId());
                Main.getZnews().server.getWorkerGroup().execute(() ->
                {
                    try
                    {
                        email.send(reg.getEmail());
                        resBuilder.addSuccess(reg.getEmail());
                    }
                    catch (MessagingException e)
                    {
                        Log.debug("Failed to send newsletter publication email to " + reg.getEmail() + ": " + e.getMessage());
                        resBuilder.addFailure(reg.getEmail(), e);
                    }
                    finally
                    {
                        finishLatch.countDown();
                    }
                });
            });
            
            // We are in an asynchronous environment, so this works
            try
            {
                finishLatch.await();
            }
            catch (InterruptedException ignored)
            {
            }
    
            NewsletterPublicationResult res = resBuilder.build();
    
            Log.out(String.format("Finished publication of \"%s\" (%s). Email send success rate: %d/%d = %.2f%%", n.getTitle(), nid, res.getNumSuccesses(), res.getNumTotal(), res.getSuccessRate() * 100));
    
        });
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
