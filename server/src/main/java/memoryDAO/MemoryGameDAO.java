package memoryDAO;
import model.GameData;
import java.util.Map;
import java.util.HashMap;

public class MemoryGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    public void clear(){
        games.clear();
    }

    public void create(GameData data){
        games.put(data.getGameID(), data);
    }

    public GameData get(int gameID){
        if(games.containsKey(gameID)){
            return games.get(gameID);

        } else{
            throw new IllegalArgumentException("Error: bad request");
        }
    }

}
