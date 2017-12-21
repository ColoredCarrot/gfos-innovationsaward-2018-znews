package de.znews.server.emai_reg;

import de.znews.server.ZNews;

import javax.mail.MessagingException;

public abstract class Email extends EmailTemplate
{
    
    protected final ZNews znews;
    
    protected Email(ZNews znews)
    {
        this.znews = znews;
    }
    
    public void send(String to) throws MessagingException
    {
        znews.emailSender.sendPlaintextAndHtml(to, getSubject(), getPlaintext(), getHtml());
    }
    
}
