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
        } catch (Exception e) {
            return ErrorService.handleException(e, res, gson);
        }

    }

}
