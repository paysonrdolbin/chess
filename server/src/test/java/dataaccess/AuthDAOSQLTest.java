package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqldao.SQLAuthDAO;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOSQLTest {
    private SQLAuthDAO sqlAuth;

    @BeforeEach
    public void setup() throws DataAccessException{
        sqlAuth = new SQLAuthDAO();
        sqlAuth.clear();
    }

    @Test
    public void testAddWorks() {
        try{
            sqlAuth.add("username", "authToken");
            assertEquals(sqlAuth.getUsername("authToken"), "username");
        } catch (DataAccessException e) {
            fail("Add test failed");
        }
    }

    @Test
    public void testAddFails() {
        try{
            sqlAuth.add("username", null);
            fail("DB failed to produce bad request error due to username being null");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testClearWorks() {
        try{
            sqlAuth.add("username", "authToken");
            sqlAuth.clear();
            assertNull(sqlAuth.getUsername("authToken"), "get request should've returned null");
        } catch (DataAccessException e) {
        }
    }

    @Test
    public void testGetUsernameWorks() {
        try{
            sqlAuth.add("newUser", "authToken");
            assertEquals(sqlAuth.getUsername("authToken"), "newUser");
        } catch (DataAccessException e) {
            fail("Add test failed");
        }
    }

    @Test
    public void testGetUsernameFails() {
        try{
            sqlAuth.getUsername(null);
        } catch (DataAccessException e) {
        }
    }


    @Test
    public void testVerifyWorks(){
        try{
            sqlAuth.add("username", "authToken");
            sqlAuth.verify("authToken");
        } catch (DataAccessException e) {
            fail("database failed to verify");
        } catch (IllegalArgumentException e){
            fail("wrong arguments were provided");
        }
    }

    @Test
    public void testVerifyFails(){
        try{
            sqlAuth.add("username", "authToken");
            sqlAuth.verify("authToken1");
            fail("DB failed to unauthorize a bad authtoken");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e){
        }
    }

    @Test
    public void testDeleteWorks(){
        try{
            sqlAuth.add("username", "authToken");
            sqlAuth.delete("authToken");
            sqlAuth.verify("authToken");
            fail("failed to delete authToken");
        } catch (DataAccessException e) {
        } catch (IllegalArgumentException e){
        }
    }

    @Test
    public void testDeleteFails(){
        try{
            sqlAuth.delete("authToken");
            fail("db failed to produce error as it tried to delete an authtoken that didn't exist");
        } catch(IllegalArgumentException e){
        } catch (DataAccessException e) {
        }
    }

}
