package dataaccess;
import memorydao.MemoryGameDAO;
import model.GameData;
import sqlDAO.SQLGameDAO;

import java.util.ArrayList;

public class GameDAO {
    private final static SQLGameDAO GAME_DB = new SQLGameDAO();

    public static void clear(){
        try{
            GAME_DB.clear();
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database was unable to clear games.");
        }
    }

    public static void create(GameData gameData){
        try{
            GAME_DB.create(gameData);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database was unable to create game");
        }
    }

    public static GameData get(int gameID){
        try{
            return GAME_DB.get(gameID);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database was unable to retrieve game");
        }
    }

    public static ArrayList<GameData> list(){
        try{
            return GAME_DB.list();
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database was unable to generate list of current games");
        }
    }

    public static void update(GameData data){
        try{
            GAME_DB.update(data);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error: Database was unable to update the current game");
        }
    }
}
