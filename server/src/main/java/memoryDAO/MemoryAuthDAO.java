package memoryDAO;
import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class MemoryAuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    public void clear(){
        auths.clear();
    }
}
