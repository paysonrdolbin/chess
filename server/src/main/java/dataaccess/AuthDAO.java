package dataaccess;

import model.AuthData;
import sqldao.SQLAuthDAO;

import java.util.UUID;

public class AuthDAO {
    private final static SQLAuthDAO AUTH_DB;

    static {
        try {
            AUTH_DB = new SQLAuthDAO();
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Error: Failed to initialize USER_DB", e);
        }
    }
    public static void clear() throws DataAccessException {
        AUTH_DB.clear();
    }

    public static AuthData add(String username) throws DataAccessException{
        String authToken = generateToken();
        AUTH_DB.add(username, authToken);
        AuthData data = new AuthData(username, authToken);
        return data;
    }

    public static void delete(String authToken) throws DataAccessException{
        AUTH_DB.delete(authToken);
    }

    public static void verify(String authToken) throws DataAccessException{
        AUTH_DB.verify(authToken);
    }

    public static String getUsername(String authToken) throws DataAccessException{
        return AUTH_DB.getUsername(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }



}
