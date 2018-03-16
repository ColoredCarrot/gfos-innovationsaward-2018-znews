package de.znews.server.emai_reg;

import de.znews.server.ZNews;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewNewsletterEmail extends Email
{
    
    @Getter(AccessLevel.NONE)
    private final EmailTemplate template;
    
    private String title, registeredEmail, nid;
    
    public NewNewsletterEmail(ZNews znews)
    {
        this(znews, znews.emailTemplates.NEW_NEWSLETTER);
    }
    
    public NewNewsletterEmail(ZNews znews, EmailTemplate template)
    {
        super(znews);
        this.template = template;
    }
    
    private Object[] a()
    {
        return new Object[] {
                "{{title}}", title,
                "{{registered_email_name}}", registeredEmail.substring(0, registeredEmail.indexOf('@')),
                "{{registered_email}}", registeredEmail,
                "{{nid}}", nid
        };
    }
    
    @Override
    public String getSubject()
    {
        return template.getSubject();
    }
    
    @Override
    public String getPlaintext()
    {
        return template.getPlaintext(a());
    }
    
    @Override
    public String getHtml()
    {
        return template.getHtml(a());
    }
    
}
