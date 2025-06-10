package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.LogoutRequest;
import service.ErrorService;
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
        } catch (Exception e) {
            return ErrorService.handleException(e, res, gson);
        }
    }
}
