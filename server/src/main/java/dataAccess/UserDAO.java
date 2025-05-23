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
        UserData data = users.get(username);
        if(data == null){
            throw new IllegalArgumentException("Error: unauthorized");
        }
        return data;
    }

//    public static void exist(String username){
//        UserData data = users.get(username);
//        if(data == null){
//            throw new IllegalArgumentException("Error: unauthorized");
//        }
//    }
}
