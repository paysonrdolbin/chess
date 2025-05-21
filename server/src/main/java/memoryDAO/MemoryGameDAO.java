package memoryDAO;
import model.GameData;
import java.util.Map;
import java.util.HashMap;

public class MemoryGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }
}
