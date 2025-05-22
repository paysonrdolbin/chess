package server;

import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // clear db
        Spark.delete("/db", new ClearHandler());
        // register user
        Spark.post("/user", new RegisterHandler());
        // login user
        Spark.post("/session", new LoginHandler());
        // logout user
        Spark.delete("/session", new LogoutHandler());
        // create game
        Spark.post("/game", new CreateGameHandler());
        // join game
        Spark.put("/game", new JoinGameHandler());
        // list games
        Spark.get("/game", new ListGamesHandler());

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
