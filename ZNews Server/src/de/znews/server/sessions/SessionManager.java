package de.znews.server.sessions;

import de.znews.server.ZNews;
import de.znews.server.auth.Authenticator;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager
{
    
    private final transient ZNews         znews;
    private final           Authenticator authenticator;
    private final Map<String, Session> sessionMap = new HashMap<>();
    
    public SessionManager(ZNews znews, Authenticator authenticator)
    {
        this.znews = znews;
        this.authenticator = authenticator;
    }
    
    public Optional<Session> addSession(String email, String password)
    {
        return authenticator.authenticate(email, password)
                            .map(admin -> sessionMap.computeIfAbsent(email, usr -> Session.newSession(znews, admin)));
    }
    
    public Optional<Session> authenticate(@Nullable String token)
    {
        ///return sessionMap.containsKey(token);
        return token == null ? Optional.empty() : sessionMap.values().stream()
                                                            .filter(s -> s.getToken().equals(token))
                                                            .findFirst();
    }
    
    public void invalidateSession(Session session)
    {
        sessionMap.remove(session.getOwnerObject().getEmail());
    }
    
    public Session requireHttpAuthentication(RequestContext ctx) throws HttpException
    {
        return Optional.ofNullable(ctx.getStringCookieParam("znews_auth"))
                       .flatMap(this::authenticate)
                       .orElseThrow(() -> new Http403ForbiddenException("Failed to authenticate"));
    }
    
    public void invalidateAllSessions()
    {
        sessionMap.clear();
    }
    
}
