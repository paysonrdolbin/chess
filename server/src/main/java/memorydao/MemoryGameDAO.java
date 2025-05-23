package memorydao;
import model.GameData;

import java.util.ArrayList;
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

    public ArrayList<GameData> list(){
        ArrayList<GameData> allGameData = new ArrayList<>();
        for(Map.Entry<Integer, GameData> entry : games.entrySet()){
            allGameData.add(entry.getValue());
        }
        return allGameData;
    }

    public void update(GameData data){
        games.put(data.getGameID(), data);
    }
}
