package dataAccess;

import memoryDAO.MemoryAuthDAO;
import model.AuthData;

public class AuthDAO {
    private final static MemoryAuthDAO authDB = new MemoryAuthDAO();

    public static void clear() {
        authDB.clear();
    }

    public static AuthData add(String username){
        AuthData data = authDB.add(username);
        return data;
    }

}
