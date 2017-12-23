package de.znews.server.sessions;

import de.znews.server.ZNews;
import de.znews.server.auth.Admin;

import java.util.UUID;

public class Session
{
    
    static Session newSession(ZNews znews, Admin admin)
    {
        return new Session(znews, admin.getUniqueId(), UUID.randomUUID().toString());
    }
    
    private final transient ZNews znews;
    private final UUID   owner;
    private final String token;
    
    private Session(ZNews znews, UUID owner, String token)
    {
        this.znews = znews;
        this.owner = owner;
        this.token = token;
    }
    
    public UUID getOwner()
    {
        return this.owner;
    }
    
    public String getToken()
    {
        return this.token;
    }
    
    public Admin getOwnerObject()
    {
        return znews.authenticator.getAdmin(getOwner()).orElseThrow(() -> new IllegalStateException("Session's owner is not registered"));
    }
    
}
