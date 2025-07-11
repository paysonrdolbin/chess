package sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.*;


public class SQLAuthDAO {

    public SQLAuthDAO() throws DataAccessException{
        SQLUserDAO userSQL = new SQLUserDAO();
        userSQL.configureDatabase(createStatements);
    }

    public void add(String username, String authToken) throws DataAccessException {
        String sqlRequest = "INSERT INTO Auths (authToken, username) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, authToken);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to add user", e);
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        String sqlRequest = "SELECT * FROM Auths WHERE authToken = ?";
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
            throw new DataAccessException("Error: unable to find user", e);
        }
    }

    public void clear() throws DataAccessException {
        String sqlRequest = "DELETE FROM Auths";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to clear auth db", e);
        }
    }

    public void delete(String authToken) throws DataAccessException {
        verify(authToken);
        String sqlRequest = "DELETE FROM Auths WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e){
            throw new IllegalArgumentException("Error: unable to delete authToken", e);
        }
    }

    public void verify(String authToken) throws DataAccessException{
        String sqlRequest = "SELECT * FROM Auths WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)){
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()){
                if(!rs.next()){
                    throw new IllegalArgumentException("Error: unauthorized");
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Error: unable to verify auth token", e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS Auths (
            authToken VARCHAR(255) PRIMARY KEY,
            username VARCHAR(255) NOT NULL
            ); 
            """
    };


}

