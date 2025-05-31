package dataaccess;

import memorydao.MemoryUserDAO;
import sqlDAO.SQLUserDAO;
import model.UserData;

import javax.xml.crypto.Data;

public class UserDAO {
    private final static SQLUserDAO USERS = new SQLUserDAO();
    public static void clear(){
        try {
            USERS.clear();
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Error: Unable to clear user db");
        }

    }
    public static void add(UserData data){
        try{
            USERS.add(data);
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Error: Unable to add user");
        }
    }

    public static UserData getUserData(String username){
        try {
            UserData data = USERS.get(username);
            if(data == null){
                throw new IllegalArgumentException("Error: unauthorized");
            }
            return data;
        } catch(DataAccessException e){
            throw new IllegalArgumentException("Error: Unable to get user");
        }

    }

}
