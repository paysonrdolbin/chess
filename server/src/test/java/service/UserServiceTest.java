package service;

import dataaccess.AuthDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import response.CreateGameResponse;
import response.ListGamesResponse;
import response.LoginResponse;
import response.RegisterResponse;
import service.GameService;
import model.GameData;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private GameService gameService = new GameService();
    private UserService userService = new UserService();
    private static ClearService clearService = new ClearService();
    private static ClearRequest clearRequest = new ClearRequest();
    @BeforeEach
    public void beforeTest(){
        clearService.clearDB(clearRequest);
    }

    @Test
    public void testRegisterWorks(){
        try {
            RegisterRequest request = new RegisterRequest("username", "password", "email");
            RegisterResponse response = userService.register(request);
            AuthDAO.verify(response.getAuthToken());
        } catch(Exception e){
            fail("Tried to register user, but recevied an error");
        }
    }

    @Test
    public void testRegisterFails(){
        try {
            RegisterRequest request = new RegisterRequest(null, "password", "email");
            userService.register(request);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    public void loginWorks(){
        try {
            RegisterRequest request = new RegisterRequest("username", "password", "email");
            RegisterResponse response = userService.register(request);
            LoginRequest loginRequest = new LoginRequest("username", "password");
            LoginResponse loginResponse = userService.login(loginRequest);
            assertNotNull(loginResponse);
        } catch(Exception e){
            fail("Tried to login, but received an error");
        }
    }

    @Test
    public void loginFails(){
        try {
            RegisterRequest request = new RegisterRequest("username", "password", "email");
            RegisterResponse response = userService.register(request);
            LoginRequest loginRequest = new LoginRequest("username", "badPassword");
            LoginResponse loginResponse = userService.login(loginRequest);
            fail("Didn't give the correct password");
        } catch(Exception e){
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void logoutWorks(){
        try {
            RegisterRequest request = new RegisterRequest("username", "password", "email");
            RegisterResponse response = userService.register(request);
            LogoutRequest logoutRequest = new LogoutRequest(response.getAuthToken());
        } catch(Exception e){
            fail("Tried to logout, but received an error");
        }
    }

    @Test
    public void logoutFails(){
        try {
            RegisterRequest request = new RegisterRequest("username", "password", "email");
            RegisterResponse response = userService.register(request);
            LogoutRequest logoutRequest = new LogoutRequest("badAuth");
            userService.logout(logoutRequest);
            fail("didn't give unauthorized message");
        } catch(Exception e){
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

}
