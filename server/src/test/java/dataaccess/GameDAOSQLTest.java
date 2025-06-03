package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqldao.SQLGameDAO;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameDAOSQLTest {
    private SQLGameDAO sqlGame;

    @BeforeEach
    public void setup() throws DataAccessException{
        sqlGame = new SQLGameDAO();
        sqlGame.clear();
    }

    @Test
    public void testClearWorks() {
        try{
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            sqlGame.clear();
            sqlGame.get(1234);
            fail("Clear didn't properly clear db");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCreateWorks() {
        try{
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            sqlGame.get(1234);
        } catch (DataAccessException e) {
            fail("Create test failed");
        }
    }

    @Test
    public void testCreateFails() {
        try{
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            fail("DB failed to produce bad request error due to a repeat game being added");
        } catch (DataAccessException e) {
        }
    }

    @Test
    public void testGetWorks() {
        try{
            sqlGame.create(new GameData(4567, null, null, "game", new ChessGame()));
            sqlGame.get(4567);
        } catch (DataAccessException e) {
            fail("Get test failed");
        }
    }

    @Test
    public void testGetFails() {
        try{
            sqlGame.get(4567);
            fail("DB failed to produce bad request error due to retrieving a user that doesn't exist");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e){
        }
    }

    @Test
    public void testListWorks(){
        try{
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            sqlGame.create(new GameData(4567, null, null, "game1", new ChessGame()));
            sqlGame.create(new GameData(7890, null, null, "game2", new ChessGame()));
            ArrayList<GameData> gameList = sqlGame.list();
            assertEquals(3, gameList.size());
        } catch (DataAccessException e) {
            fail("Wasn't able to complete listing test");
        }
    }

    @Test
    public void testListFails() {
        try {
            ArrayList<GameData> gameList = sqlGame.list();
            assertEquals(0, gameList.size(), "Expected no games in empty DB");
        } catch (DataAccessException e) {
            fail("List threw an error on an empty database");
        }
    }

    @Test
    public void testUpdateWorks() {
        try {
            sqlGame.create(new GameData(1234, null, null, "game", new ChessGame()));
            sqlGame.update(new GameData(1234, "a", "b", "game", new ChessGame()));
            GameData gameData = sqlGame.get(1234);
            assertEquals("a", gameData.getWhiteUsername());
        } catch (DataAccessException e) {
            fail("unable to update");
        }
    }

    @Test
    public void testUpdateFails(){
        try {
            sqlGame.update(new GameData(1234, "a", "b", "game", new ChessGame()));
            fail("db updated a game that didn't exist");
        } catch (DataAccessException e) {
        }
    }
}
