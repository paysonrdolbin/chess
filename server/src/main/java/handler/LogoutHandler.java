package handler;

import com.google.gson.Gson;
import request.LogoutRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LogoutHandler implements Route {

    private final Gson gson = new Gson();
    // default constructor
    public LogoutHandler(){
    }
    @Override
    public Object handle(Request req, Response res){
        String authToken = req.headers("authorization");
        try{
            UserService service = new UserService();
            LogoutRequest request = new LogoutRequest(authToken);
            service.logout(request);

            res.status(200);
            return "{}";
        } catch (IllegalArgumentException e){
            res.status(403);
            return gson.toJson(Map.of("message",e.getMessage()));
        }
    }
}
