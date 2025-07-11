package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import response.*;
import request.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService service = new GameService();
    private static UserService userService = new UserService();
    private static String userAuthorizedAuth;
    @BeforeAll
    public static void beforeTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
        userAuthorizedAuth = userService.register(registerRequest).getAuthToken();
    }

    @Test
    public void testCreateWorks(){
        try {
            CreateGameRequest request = new CreateGameRequest(userAuthorizedAuth, "gameName");
            CreateGameResponse response = service.create(request);
            assertNotNull(response);
            assertTrue(response.getGameID() >= 1111 && response.getGameID() <= 9999);
        } catch(Exception e){
            fail("Tried to create game, but recevied an error");
        }
    }

    @Test
    public void testCreateFails() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest("fakeAuthorizationToken", "gameName");
        try {
            CreateGameResponse response = service.create(request);
            assertNotNull(response);
            service.create(request);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void joinGameWorks(){
        try {
            CreateGameRequest createRequest = new CreateGameRequest(userAuthorizedAuth, "gameName");
            CreateGameResponse createResponse = service.create(createRequest);
            JoinGameRequest request = new JoinGameRequest(userAuthorizedAuth, "WHITE", createResponse.getGameID());
            service.join(request);
        } catch(Exception e){
            fail("Tried to join game, but received an error");
        }
    }

    @Test
    public void joinGameFails(){
        try {
            CreateGameRequest createRequest = new CreateGameRequest(userAuthorizedAuth, "gameName");
            CreateGameResponse createResponse = service.create(createRequest);
            JoinGameRequest request = new JoinGameRequest("badUserAuth", "WHITE", createResponse.getGameID());
            service.join(request);
            fail("Expected IllegalArgumentException was not thrown");
        } catch(Exception e){

        }
    }

    @Test
    public void listGameWorks() throws DataAccessException{
        ClearService clearService = new ClearService();
        ClearRequest clearRequest = new ClearRequest();
        clearService.clearDB(clearRequest);
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
        userAuthorizedAuth = userService.register(registerRequest).getAuthToken();
        CreateGameRequest createRequest = new CreateGameRequest(userAuthorizedAuth, "gameName");
        CreateGameResponse createResponse = service.create(createRequest);
        CreateGameRequest createRequest1 = new CreateGameRequest(userAuthorizedAuth, "gameName1");
        CreateGameResponse createResponse1 = service.create(createRequest);
        CreateGameRequest createRequest2 = new CreateGameRequest(userAuthorizedAuth, "gameName2");
        CreateGameResponse createResponse2 = service.create(createRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(userAuthorizedAuth);
        ListGamesResponse response = service.list(listGamesRequest);
        assertEquals(3, response.getGames().size());
    }

    @Test
    public void listGameFails(){
        try {
            ClearService clearService = new ClearService();
            ClearRequest clearRequest = new ClearRequest();
            clearService.clearDB(clearRequest);
            RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
            userAuthorizedAuth = userService.register(registerRequest).getAuthToken();
            CreateGameRequest createRequest = new CreateGameRequest(userAuthorizedAuth, "gameName");
            CreateGameResponse createResponse = service.create(createRequest);
            CreateGameRequest createRequest1 = new CreateGameRequest(userAuthorizedAuth, "gameName1");
            CreateGameResponse createResponse1 = service.create(createRequest);
            CreateGameRequest createRequest2 = new CreateGameRequest(userAuthorizedAuth, "gameName2");
            CreateGameResponse createResponse2 = service.create(createRequest);
            ListGamesRequest listGamesRequest = new ListGamesRequest("badAuthToken");
            ListGamesResponse response = service.list(listGamesRequest);
            fail("Unauthrozied Error was not received");
        } catch (Exception e) {
        }
    }

}
