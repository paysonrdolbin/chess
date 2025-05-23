package dataaccess;

import memorydao.MemoryAuthDAO;
import model.AuthData;

public class AuthDAO {
    private final static MemoryAuthDAO AUTH_DB = new MemoryAuthDAO();

    public static void clear() {
        AUTH_DB.clear();
    }

    public static AuthData add(String username){
        AuthData data = AUTH_DB.add(username);
        return data;
    }

    public static void delete(String authToken){
        AUTH_DB.delete(authToken);
    }

    public static void verify(String authToken){
        AUTH_DB.verify(authToken);
    }

    public static String getUsername(String authToken){
        return AUTH_DB.getUsername(authToken);
    }
}
