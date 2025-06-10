package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.ObserveGameRequest;
import service.ErrorService;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ObserveGameHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public ObserveGameHandler(){

    }

    @Override
    public Object handle(Request req, Response res){
        int gameID = gson.fromJson(req.body(), int.class);
        String authToken = req.headers("authorization");
        try{
            ObserveGameRequest request = new ObserveGameRequest(authToken, gameID);
            GameService service = new GameService();
            service.observe(request);
            return "{}";
        } catch (IllegalArgumentException e){
            int statusCode = ErrorService.errorCode(e.getMessage());
            res.status(statusCode);
            return gson.toJson(Map.of("message", e.getMessage(), "status", statusCode));
        } catch (DataAccessException e){
            res.status(500);
            return gson.toJson(Map.of("message", e.getMessage(), "status", 500));
        }

    }

}
