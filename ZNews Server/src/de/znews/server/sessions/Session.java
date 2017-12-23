package de.znews.server.sessions;

import de.znews.server.auth.Admin;

import java.util.UUID;

public class Session
{
    
    static Session newSession(Admin admin)
    {
        return new Session(admin.getUniqueId(), UUID.randomUUID().toString());
    }
    
    private final UUID   owner;
    private final String token;
    
    private Session(UUID owner, String token)
    {
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
    
}
