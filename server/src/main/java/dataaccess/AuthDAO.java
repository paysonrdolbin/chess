package dataaccess;

import memorydao.MemoryAuthDAO;
import model.AuthData;
import sqlDAO.SQLAuthDAO;

import java.util.UUID;

public class AuthDAO {
    private final static SQLAuthDAO AUTH_DB = new SQLAuthDAO();

    public static void clear() {
        try{
            AUTH_DB.clear();
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database failed to clear");
        }
    }

    public static AuthData add(String username){
        try{
            String authToken = generateToken();
            AUTH_DB.add(username, authToken);
            AuthData data = new AuthData(username, authToken);
            return data;
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database failed to add");
        }
    }

    public static void delete(String authToken){
        try{
            AUTH_DB.delete(authToken);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database failed to delete authToken");
        }
    }

    public static void verify(String authToken){
        try{
            AUTH_DB.verify(authToken);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database failed to verify authToken");
        }
    }

    public static String getUsername(String authToken){
        try{
            return AUTH_DB.getUsername(authToken);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database failed to return username");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
