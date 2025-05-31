package sqlDAO;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO{
    private final Gson gson = new Gson();

    public void clear() throws DataAccessException {
        String sqlRequest = "DELETE FROM Games";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games from db", e);
        }
    }

    public void create(GameData data) throws DataAccessException {
        String sqlRequest = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?,?)";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            String gameJson = gson.toJson(data.getGame());
            statement.setInt(1, data.getGameID());
            statement.setString(2, data.getWhiteUsername());
            statement.setString(3, data.getBlackUsername());
            statement.setString(4, data.getGameName());
            statement.setString(5, gameJson);

        } catch (SQLException e){
            throw new DataAccessException("Unable to create game", e);
        }
    }

    public GameData get(int gameID) throws DataAccessException {
        String sqlRequest = "SELECT * FROM Games WHERE gameID = ?";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.setInt(1, gameID);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                        gameID,
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                            );
                }
                throw new DataAccessException("Game not found");
            }

        } catch(SQLException e){
            throw new DataAccessException("Unable to retrieve game", e);
        }
    }

    public ArrayList<GameData> list() throws DataAccessException{
        ArrayList<GameData> gameList = new ArrayList<>();
        String sqlRequest = "SELECT * FROM GAMES";
        try(Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlRequest);
        ResultSet rs = statement.executeQuery()){
            while(rs.next()){
                ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                GameData data = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                );
                gameList.add(data);
            }
            return gameList;
        } catch (SQLException e){
            throw new DataAccessException("Unable to list games", e);
        }
    }

    public void update(GameData data) throws DataAccessException{
        String sqlRequest = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try(Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            String gameJson = gson.toJson(data.getGame());
            statement.setInt(5, data.getGameID());
            statement.setString(1, data.getWhiteUsername());
            statement.setString(2, data.getBlackUsername());
            statement.setString(3, data.getGameName());
            statement.setString(4, gameJson);
            if(statement.executeUpdate() == 0){
                throw new DataAccessException("No game found under this ID");
            }
        } catch (SQLException e){
            throw new DataAccessException("Unable to update a game", e);
        }
    }

}
