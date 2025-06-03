package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import sqldao.SQLUserDAO;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOSQLTest {
    private SQLUserDAO sqlUser;

    @BeforeEach
    public void setup() throws DataAccessException{
        sqlUser = new SQLUserDAO();
        sqlUser.clear();
    }

    @Test
    public void testAddWorks() {
        try{
            sqlUser.add(new UserData("username", "password", "email"));
            sqlUser.get("username");
        } catch (DataAccessException e) {
            fail("Add test failed");
        }
    }

    @Test
    public void testAddFails() {
        try{
            sqlUser.add(new UserData(null, "password", "email"));
            fail("DB failed to produce bad request error due to username being null");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetWorks() {
        try{
            sqlUser.add(new UserData("newUser", "password", "email"));
            sqlUser.get("newUser");
        } catch (DataAccessException e) {
            fail("Add test failed");
        }
    }

    @Test
    public void testGetFails() {
        try{
            sqlUser.get(null);
        } catch (DataAccessException e) {
        }
    }

    @Test
    public void testClearWorks() {
        try{
            sqlUser.add(new UserData("username", "password", "email"));
            sqlUser.clear();
            assertNull(sqlUser.get("username"), "get request should've returned null");
        } catch (DataAccessException e) {
        }
    }

}
