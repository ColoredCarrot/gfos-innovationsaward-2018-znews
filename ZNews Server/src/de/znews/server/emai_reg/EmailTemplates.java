package de.znews.server.emai_reg;

import de.znews.server.ZNews;

import java.io.File;

public class EmailTemplates
{
    
    public final EmailTemplate DOUBLE_OPT_IN;
    public final EmailTemplate NEW_NEWSLETTER;
    
    public EmailTemplates(ZNews znews)
    {
        DOUBLE_OPT_IN = new FileEmailTemplate(znews, "/resources/email_templates/double_opt_in/double_opt_in.plaintext.txt", null, new File(znews.config.getEmailConfig()
                                                                                                                                                        .getTemplatePathDoubleOptIn()), /*FIXME: Make email subject configurable*/"Confirm Subscription");
        NEW_NEWSLETTER = new FileEmailTemplate(znews,
                "/resources/email_templates/new_newsletter/new_newsletter.plaintext.txt",
                "/resources/email_templates/new_newsletter/new_newsletter.html.html",
                new File(znews.config.getEmailConfig()
                                     .getTemplatePathNewNewsletter()), /*FIXME: Make email subject configurable*/"New Newsletter");
    }
    
}
