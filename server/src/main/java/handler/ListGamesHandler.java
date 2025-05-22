package handler;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import request.ListGamesRequest;

public class ListGamesHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public ListGamesHandler(){}

    @Override
    public Object handle(Request req, Response res){
        String authToken = req.headers("authorization");
        GameService service = new GameService();
        ListGamesRequest request = new ListGamesRequest(authToken);
        service.list(request);
    }
}
