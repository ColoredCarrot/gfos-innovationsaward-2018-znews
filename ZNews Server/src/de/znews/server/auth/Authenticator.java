package de.znews.server.auth;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.reflect.JsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import com.coloredcarrot.jsonapi.reflect.JsonSerializer;
import de.znews.server.ZNews;
import de.znews.server.resources.RequestContext;
import de.znews.server.resources.exception.Http403ForbiddenException;
import de.znews.server.resources.exception.HttpException;
import de.znews.server.sessions.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Authenticator implements JsonSerializable
{
    
    private transient ZNews znews;
    private Map<UUID, Admin> admins = new HashMap<>();
    
    public void setZNewsInstance(ZNews instance)
    {
        this.znews = instance;
    }
    
    private Optional<Admin> getPossibleAdmin(String email)
    {
        return admins.values().stream().filter(a -> a.getEmail().equalsIgnoreCase(email)).findFirst();
    }
    
    public Admin addAdmin(String email, String name, String password)
    {
        getPossibleAdmin(email).ifPresent(admin -> { throw new IllegalArgumentException("An admin with that email already exists"); });
        Admin admin = Admin.create(email, name, password);
        admins.put(admin.getUniqueId(), admin);
        return admin;
    }
    
    public Optional<Admin> authenticate(String email, String password)
    {
        return getPossibleAdmin(email).filter(admin -> admin.checkPassword(password));
    }
    
    public Session requireHttpAuthentication(RequestContext ctx) throws HttpException
    {
        return Optional.ofNullable(ctx.getStringCookieParam("znews_auth"))
                       .flatMap(znews.sessionManager::isAuthenticated)
                       .orElseThrow(() -> new Http403ForbiddenException("Failed to authenticate"));
        
        /*if (!ctx.hasCookieParam("znews_auth"))
            throw new Http403ForbiddenException("Authentication cookie missing");
        String auth = ctx.getStringCookieParam("znews_auth");
        if (!znews.sessionManager.isAuthenticated(auth))
            throw new Http403ForbiddenException("Authentication cookie invalid");*/
        
    }
    
    @JsonSerializer
    private JsonNode serialize()
    {
        JsonArray.Builder json = JsonArray.createBuilder();
        for (Admin admin : admins.values())
            json.add(admin);
        return json.build();
    }
    
    @JsonDeserializer
    private static Authenticator deserialize(JsonArray json)
    {
        Authenticator authenticator = new Authenticator();
        authenticator.admins = json.getContents().stream()
                                   .map(adminJson -> Json.deserialize(adminJson, Admin.class))
                                   .collect(Collectors.toMap(Admin::getUniqueId, Function.identity()));
        return authenticator;
    }
    
}
