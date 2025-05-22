package memoryDAO;
import model.AuthData;

import java.util.*;

public class MemoryAuthDAO {
    private final Map<String, String> authDB = new HashMap<>();

    public void clear(){
        authDB.clear();
    }

    public AuthData add(String username){
        String token = generateToken();
        AuthData auth = new AuthData(username, token);
        authDB.put(token, username);
        return auth;
    }

    public void delete(String authToken){
        if(!authDB.containsKey(authToken)){
            throw new IllegalArgumentException("Error: unauthorized");
        }
        authDB.remove(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
