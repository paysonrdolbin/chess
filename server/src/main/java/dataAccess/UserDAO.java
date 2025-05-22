package dataAccess;

import memoryDAO.MemoryUserDAO;
import model.UserData;

public class UserDAO {
    private final static MemoryUserDAO users = new MemoryUserDAO();
    public static void clear(){
        users.clear();
    }
    public static void add(UserData data){
        users.add(data);
    }

    public static UserData getUserData(String username){
        return users.get(username);
    }
}
