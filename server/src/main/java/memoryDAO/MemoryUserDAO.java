package memoryDAO;
import model.UserData;
import java.util.Map;
import java.util.HashMap;

public class MemoryUserDAO {
    private final Map<String, UserData> userDB = new HashMap<>();

    public void clear(){
        userDB.clear();
    }

    public void add(UserData data){
        if(userDB.containsKey(data.getUsername())){
            throw new IllegalArgumentException("Error: user already taken");
        }
        userDB.put(data.getUsername(), data);
    }
}
