package sqlDAO;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import java.sql.*;


public class SQLUserDAO implements UserDAO {

    @Override
    public void add(UserData user) throws DataAccessException {
        String sqlRequest = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlRequest)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to add user", e);
        }
    }

    @Override
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
            throw new DataAccessException("Unable to find user", e);
        }
    }

}

