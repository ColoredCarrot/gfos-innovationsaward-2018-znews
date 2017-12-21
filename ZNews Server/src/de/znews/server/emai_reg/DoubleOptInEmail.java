package de.znews.server.emai_reg;

import de.znews.server.ZNews;
import lombok.Getter;
import lombok.Setter;

public class DoubleOptInEmail extends Email
{
    
    private final EmailTemplate template;
    
    @Getter
    @Setter
    private String registeredEmail;
    
    public DoubleOptInEmail(ZNews znews)
    {
        this(znews, null/*FIXME: Add DoubleOptInEmailTemplate*/);
    }
    
    public DoubleOptInEmail(ZNews znews, EmailTemplate template)
    {
        super(znews);
        this.template = template;
    }
    
    @Override
    public String getSubject()
    {
        return template.getSubject();
    }
    
    @Override
    public String getPlaintext()
    {
        return template.getPlaintext("{{registered_email}}", registeredEmail);
    }
    
    @Override
    public String getHtml()
    {
        return template.getHtml("{{registered_email}}", registeredEmail);
    }
    
}
