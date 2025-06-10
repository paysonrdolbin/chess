package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import response.ListGamesResponse;
import service.ErrorService;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import request.ListGamesRequest;

import java.util.Map;

public class ListGamesHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public ListGamesHandler(){}

    @Override
    public Object handle(Request req, Response res){
        String authToken = req.headers("authorization");
        try {
            GameService service = new GameService();
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResponse response = service.list(request);
            res.status(200);
            return gson.toJson(response);
        } catch (Exception e) {
            return ErrorService.handleException(e, res, gson);
        }
    }
}
