package java.client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import response.*;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");

        String username = "user" + UUID.randomUUID();
        String password = "password";
        String email = "user@example.com";

        facade.register(username, password, email);
        facade.login(username, password);

    }

    @Test
    public void registerFailsDuplicateUser() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();
        String password = "password";
        String email = "user@example.com";

        facade.register(username, password, email);

        Assertions.assertThrows(ResponseException.class, () ->
                facade.register(username, password, email)
        );
    }

    @Test
    public void loginWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();
        String password = "password";

        facade.register(username, password, username + "@example.com");
        facade.login(username, password);
    }

    @Test
    public void loginFailWrongPassword() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();
        String password = "password";

        facade.register(username, password, username + "@example.com");

        Assertions.assertThrows(ResponseException.class, () ->
                facade.login(username, "wrongpassword")
        );
    }

    @Test
    public void createWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();

        facade.register(username, "password", username + "@example.com");
        int gameID = facade.create("game");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createFailsBadAuth() {
        ServerFacade noAuthFacade = new ServerFacade("http://localhost:0");

        Assertions.assertThrows(ResponseException.class, () ->
                noAuthFacade.create("game")
        );
    }

    @Test
    public void listWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();

        facade.register(username, "password", username + "@example.com");
        ListGamesResponse response = facade.list();

        Assertions.assertNotNull(response.getGames());
    }

    @Test
    public void listFailsNoAuth() {
        ServerFacade noAuthFacade = new ServerFacade("http://localhost:0");

        Assertions.assertThrows(ResponseException.class, () ->
                noAuthFacade.list()
        );
    }

    @Test
    public void joinWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();

        facade.register(username, "password", username + "@example.com");
        int gameID = facade.create("joinGame");

        facade.join(gameID, "WHITE");
    }

    @Test
    public void joinFailsInvalidGame() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();

        facade.register(username, "password", username + "@example.com");

        Assertions.assertThrows(ResponseException.class, () ->
                facade.join(-1, "WHITE")
        );
    }

    @Test
    public void logoutWorks() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:0");
        String username = "user" + UUID.randomUUID();

        facade.register(username, "password", username + "@example.com");
        facade.logout(); // should not throw
    }

    @Test
    public void logoutFailsNoAuth() {
        ServerFacade noAuthFacade = new ServerFacade("http://localhost:0");

        Assertions.assertThrows(ResponseException.class, () ->
                noAuthFacade.logout()
        );
    }

}
