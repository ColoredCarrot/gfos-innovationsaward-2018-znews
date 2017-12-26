package de.znews.server.emai_reg;

import com.google.common.net.MediaType;
import de.znews.server.ZNews;
import de.znews.server.lib.Wrapper;
import org.jetbrains.annotations.Nullable;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

public class EmailSender
{
    
    private final ZNews znews;
    
    public EmailSender(ZNews znews)
    {
        this.znews = znews;
    }
    
    public void send(Function<? super Session, ? extends Message> msgGen) throws MessagingException
    {
        
        Wrapper<Authenticator> authenticatorWrapper = new Wrapper<>();
        
        Properties props = znews.config.getEmailConfig().toJavaxMailProps(authenticatorWrapper::set);
        
        Session session = Session.getInstance(props, authenticatorWrapper.get());
        session.setDebug(znews.config.getEmailConfig().isDebug());
        
        Message msg = null;
        try
        {
            msg = msgGen.apply(session);
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof MessagingException)
                throw (MessagingException) e.getCause();
            throw e;
        }
        
        if (msg.getSentDate() == null)
            msg.setSentDate(new Date());
        
        Transport.send(msg);
        
    }
    
    public void send(String to, String subject, Supplier<? extends Multipart> contentSupplier) throws MessagingException
    {
        send(session ->
        {
            MimeMessage msg = null;
            try
            {
                msg = new MimeMessage(session);
                msg.setFrom(znews.config.getEmailConfig().getFrom());
                msg.setRecipients(Message.RecipientType.TO, to);
                msg.setSubject(subject);
                msg.setContent(contentSupplier.get());
            }
            catch (MessagingException e)
            {
                throw new RuntimeException(e);
            }
            return msg;
        });
    }
    
    public void send(String to, String subject, Map<MediaType, Object> alternatives) throws MessagingException
    {
        send(to, subject, () ->
        {
            try
            {
                Multipart m = new MimeMultipart("alternative");
                
                for (Map.Entry<MediaType, Object> alternative : alternatives.entrySet())
                {
                    BodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setContent(alternative.getValue(), alternative.getKey().toString());
                    m.addBodyPart(bodyPart);
                }
                
                return m;
            }
            catch (MessagingException e)
            {
                throw new RuntimeException(e);
            }
        });
    }
    
    public void sendPlaintextAndHtml(String to, String subject, String plaintext, @Nullable String html) throws MessagingException
    {
        Map<MediaType, Object> alternatives = new HashMap<>();
        alternatives.put(MediaType.PLAIN_TEXT_UTF_8, plaintext);
        if (html != null)
            alternatives.put(MediaType.HTML_UTF_8, html);
        send(to, subject, alternatives);
    }
    
}
