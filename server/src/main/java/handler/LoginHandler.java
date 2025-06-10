package handler;

import dataaccess.DataAccessException;
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
        try{
            UserService service = new UserService();
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            LoginResponse servResponse = service.login(loginRequest);
            res.status(200);
            return gson.toJson(servResponse);
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

