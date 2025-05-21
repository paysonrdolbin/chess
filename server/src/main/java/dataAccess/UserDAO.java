package dataAccess;

import memoryDAO.MemoryUserDAO;

public class UserDAO {
    private final static MemoryUserDAO users = new MemoryUserDAO();
    public static void clear(){
        users.clear();
    }
}
