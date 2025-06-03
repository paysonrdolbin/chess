package dataaccess;

import sqldao.SQLUserDAO;
import model.UserData;

public class UserDAO {
    private final static SQLUserDAO USER_DB;

    static {
        try {
            USER_DB = new SQLUserDAO();
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Error: Failed to initialize USER_DB", e);
        }
    }

    public static void clear() throws DataAccessException {
        USER_DB.clear();
    }
    public static void add(UserData data) throws DataAccessException{
        USER_DB.add(data);
    }

    public static UserData getUserData(String username) throws DataAccessException{
            UserData data = USER_DB.get(username);
            if(data == null){
                throw new IllegalArgumentException("Error: unauthorized");
            }
            return data;
    }

}
