package dataaccess;

import memorydao.MemoryUserDAO;
import model.UserData;

public class UserDAO {
    private final static MemoryUserDAO USERS = new MemoryUserDAO();
    public static void clear(){
        USERS.clear();
    }
    public static void add(UserData data){
        USERS.add(data);
    }

    public static UserData getUserData(String username){
        UserData data = USERS.get(username);
        if(data == null){
            throw new IllegalArgumentException("Error: unauthorized");
        }
        return data;
    }

}
