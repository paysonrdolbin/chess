package sqlDAO;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import memorydao.MemoryAuthDAO;
import java.sql.*;


public class SQLAuthDAO {

    public void add(String username, String authToken) throws DataAccessException {
        String sqlRequest = "INSERT INTO Auths (authToken, username) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, authToken);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to add user", e);
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        String sqlRequest = "SELECT * FROM Users WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to find user", e);
        }
    }

    public void clear() throws DataAccessException {
        String sqlRequest = "DELETE FROM Auths";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth db", e);
        }
    }

    public int size() throws DataAccessException {
        String sqlRequest = "SELECT COUNT(*) FROM Auths";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest);
            ResultSet rs = statement.executeQuery()) {
            if(rs.next()){
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
             throw new DataAccessException("Unable to return auth db size", e);
        }
    }

    public void delete(String authToken) throws DataAccessException {
        String sqlRequest = "DELETE FROM Auths WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e){
            throw new IllegalArgumentException("Unable to delete authToken", e);
        }
    }

    public boolean verify(String authToken) throws DataAccessException{
        String sqlRequest = "SELECT FROM Auths WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    return true;
                } else{
                    return false;
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Unable to verify authToken", e);
        }
    }

}

