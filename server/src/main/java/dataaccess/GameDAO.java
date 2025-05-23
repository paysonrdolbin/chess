package dataaccess;
import memorydao.MemoryGameDAO;
import model.GameData;

import java.util.ArrayList;

public class GameDAO {
    private final static MemoryGameDAO GAME_DB = new MemoryGameDAO();

    public static void clear(){
        GAME_DB.clear();
    }

    public static void create(GameData gameData){
        GAME_DB.create(gameData);
    }

    public static GameData get(int gameID){
        return GAME_DB.get(gameID);
    }

    public static ArrayList<GameData> list(){
        return GAME_DB.list();
    }

    public static void update(GameData data){
        GAME_DB.update(data);
    }
}
