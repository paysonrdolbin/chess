package dataaccess;
import model.GameData;
import sqldao.SQLGameDAO;

import java.util.ArrayList;

public class GameDAO {
    private final static SQLGameDAO GAME_DB;

    static {
        try {
            GAME_DB = new SQLGameDAO();
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Error: Failed to initialize Game_DB", e);
        }
    }

    public static void clear() throws DataAccessException{
        GAME_DB.clear();
    }

    public static void create(GameData gameData) throws DataAccessException{
        GAME_DB.create(gameData);
    }

    public static GameData get(int gameID) throws DataAccessException{
        return GAME_DB.get(gameID);
    }

    public static ArrayList<GameData> list() throws DataAccessException{
        return GAME_DB.list();
    }

    public static void update(GameData data) throws DataAccessException{
        GAME_DB.update(data);
    }
}
