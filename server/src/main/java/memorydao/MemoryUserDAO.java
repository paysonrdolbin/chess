package memorydao;
import model.UserData;
import java.util.Map;
import java.util.HashMap;

public class MemoryUserDAO {
    private final Map<String, UserData> userDB = new HashMap<>();

    public void clear(){
        userDB.clear();
    }

    public void add(UserData data){
        // if the username is already taken.
        if(userDB.containsKey(data.getUsername())){
            throw new IllegalArgumentException("Error: already taken");
        }
        userDB.put(data.getUsername(), data);
    }

    public UserData get(String username){
        // if the user isn't in the db.
        if(!userDB.containsKey(username)){
            return null;
        }
        return userDB.get(username);
    }

}

