package memoryDAO;
import model.AuthData;

import java.util.*;

public class MemoryAuthDAO {
    private final Map<String, ArrayList<AuthData> > auths = new HashMap<>();

    public void clear(){
        auths.clear();
    }

    public AuthData add(String username){
        String token = generateToken();
        AuthData auth = new AuthData(username, token);
        if(auths.containsKey(username)){
            auths.get(username).add(auth);
        }else{
            ArrayList<AuthData> authList = new ArrayList<>();
            authList.add(auth);
            auths.put(username, authList);
        }
        return auth;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
