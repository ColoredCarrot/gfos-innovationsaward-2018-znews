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
        this(znews, znews.emailTemplates.DOUBLE_OPT_IN);
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
    
    private Object[] a()
    {
        return new Object[] {
                "{{full_address}}", znews.config.getFullExternalAddress(),
                "{{registered_email}}", registeredEmail,
                "{{registered_email_name}}", registeredEmail.substring(0, registeredEmail.indexOf('@')) };
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
