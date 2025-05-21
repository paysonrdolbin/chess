package memoryDAO;
import model.AuthData;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    public void clear(){
        auths.clear();
    }

    public AuthData add(String username){
        AuthData auth = new AuthData(username, generateToken());
        auths.put(username, auth);
        return auth;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
