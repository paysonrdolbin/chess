package handler;

import model.UserData;
import com.google.gson.Gson;
import request.LoginRequest;
import response.LoginResponse;
import service.ErrorService;
import service.UserService;
import spark.Route;
import spark.Request;
import spark.Response;
import java.util.Map;


public class LoginHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public LoginHandler() {
    }
    @Override
    public Object handle(Request req, Response res){
        UserData user = gson.fromJson(req.body(), UserData.class);
        try{
            UserService service = new UserService();
            LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
            LoginResponse servResponse = service.login(loginRequest);
            res.status(200);
            return gson.toJson(servResponse);
        } catch (IllegalArgumentException e){
            res.status(ErrorService.errorCode(e.getMessage()));
            return gson.toJson(Map.of("message",e.getMessage()));
        }



    }
}

