package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.JoinJsonBody;
import service.ErrorService;
import service.GameService;
import spark.Request;
import spark.Response;
import request.JoinGameRequest;
import spark.Route;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public JoinGameHandler(){

    }

    @Override
    public Object handle(Request req, Response res){
        JoinJsonBody joinJson = gson.fromJson(req.body(), JoinJsonBody.class);
        String authToken = req.headers("authorization");
        try{
            String color = joinJson.getColor();
            int gameID = joinJson.getGameID();
            JoinGameRequest request = new JoinGameRequest(authToken, color, gameID);
            GameService service = new GameService();
            service.join(request);
            return "{}";
        } catch (Exception e) {
            return ErrorService.handleException(e, res, gson);
        }

    }

}
