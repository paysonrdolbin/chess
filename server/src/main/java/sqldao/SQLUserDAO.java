package sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;
import java.sql.*;


public class SQLUserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public void add(UserData user) throws DataAccessException {
        String sqlRequest = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")){
                throw new IllegalArgumentException("Error: already taken", e);
            }
            throw new DataAccessException("Error: unable to add user", e);
        }
    }

    public UserData get(String username) throws DataAccessException {
        String sqlRequest = "SELECT * FROM Users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error in finding user", e);
        }
    }

    public void clear() throws DataAccessException {
        String sqlRequest = "DELETE FROM Users";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to clear user db", e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS Users (
            username VARCHAR(255) PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255)
            );
            """
    };

    public void configureDatabase(String[] statements) throws DataAccessException{
        DatabaseManager.createDatabase();
        try(Connection connection = DatabaseManager.getConnection()){
            for(var statement : statements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to connect to database", e);
        }
    }

}

