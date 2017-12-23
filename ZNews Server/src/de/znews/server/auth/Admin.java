package de.znews.server.auth;

import com.coloredcarrot.jsonapi.reflect.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements JsonSerializable
{
    
    public static Admin create(String email, String name, String password)
    {
        return new Admin(UUID.randomUUID(), email, name, BCrypt.hashpw(password, BCrypt.gensalt()));
    }
    
    private UUID   uniqueId;
    private String email;
    private String name;
    private String passwordHash;
    
    private void changePasswordTo0(String newPassword)
    {
        this.passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }
    
    public boolean changePasswordTo(String newPassword, String oldPassword)
    {
        if (BCrypt.checkpw(oldPassword, this.passwordHash))
        {
            changePasswordTo0(newPassword);
            return true;
        }
        return false;
    }
    
    public boolean checkPassword(String password)
    {
        return BCrypt.checkpw(password, this.passwordHash);
    }
    
}
