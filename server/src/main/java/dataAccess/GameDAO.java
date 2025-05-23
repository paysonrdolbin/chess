package dataAccess;
import chess.ChessGame;
import memoryDAO.MemoryGameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

public class GameDAO {
    private final static MemoryGameDAO gameDB = new MemoryGameDAO();

    public static void clear(){
        gameDB.clear();
    }

    public static void create(GameData gameData){
        gameDB.create(gameData);
    }

    public static GameData get(int gameID){
        return gameDB.get(gameID);
    }

    public static ArrayList<GameData> list(){
        return gameDB.list();
    }

    public static void update(GameData data){
        gameDB.update(data);
    }
}
