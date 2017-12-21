package de.znews.server.emai_reg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfig
{
    
    private int      port;
    private String   host;
    private String   from;
    private boolean  auth;
    private String   authUsr;
    private String   authPw;
    private Protocol protocol;
    private boolean  debug;
    
    public Properties toJavaxMailProps(Consumer<? super Authenticator> authenticatorConsumer)
    {
        Properties p = new Properties();
        
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", port);
        
        switch (protocol)
        {
        case SMTPS:
            p.put("mail.smtp.ssl.enable", true);
            break;
        case TLS:
            p.put("mail.smtp.starttls.enable", true);
            break;
        }
        
        if (auth)
        {
            p.put("mail.smtp.auth", true);
            authenticatorConsumer.accept(new Authenticator()
            {
                private PasswordAuthentication pa = new PasswordAuthentication(authUsr, authPw);
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return pa;
                }
            });
        }
        
        return p;
    }
    
    public enum Protocol
    {
        SMTP, SMTPS, TLS
    }
    
}
