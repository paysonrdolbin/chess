package dataAccess;
import chess.ChessGame;
import memoryDAO.MemoryGameDAO;
import model.GameData;
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

}
