package handler;

import dataaccess.DataAccessException;
import request.CreateGameRequest;
import response.CreateGameResponse;
import service.ErrorService;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import request.CreateJsonBody;

import javax.xml.crypto.Data;
import java.util.Map;

public class CreateGameHandler implements Route {
    private final Gson gson = new Gson();
    // default constructor
    public CreateGameHandler(){}

    @Override
    public Object handle(Request req, Response res){
        String authToken = req.headers("authorization");
        CreateJsonBody jsonBody = gson.fromJson(req.body(), CreateJsonBody.class);
        String gameName = jsonBody.getGameName();
        try{
            CreateGameRequest request = new CreateGameRequest(authToken, gameName);
            GameService service = new GameService();
            CreateGameResponse response = service.create(request);
            res.status(200);
            return gson.toJson(response);
        } catch(IllegalArgumentException e){
            res.status(ErrorService.errorCode(e.getMessage()));
            return gson.toJson(Map.of("message",e.getMessage()));
        } catch(DataAccessException e){
            res.status(500);
            return gson.toJson(Map.of("message",e.getMessage()));
        }

    }

}
