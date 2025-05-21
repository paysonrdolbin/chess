package handler;

import chess.InvalidMoveException;
import model.UserData;
import com.google.gson.Gson;
import request.RegisterRequest;
import response.RegisterResponse;
import service.UserService;
import spark.Route;
import spark.Request;
import spark.Response;
import java.util.Map;


public class LoginHandler implements Route {
    // default constructor
    private final Gson gson = new Gson();

    public LoginHandler() {
    }
    @Override
    public Object handle(Request req, Response res){
        UserData user = gson.fromJson(req.body(), UserData.class);
        try{
            UserService service = new UserService();
            LoginRequest loginRequest = new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());
            LoginResponse servResponse = service.login(loginRequest);
            res.status(200);
            return gson.toJson(servResponse);
        } catch (IllegalArgumentException e){
            res.status(403);
            return gson.toJson(Map.of("message",e.getMessage()));
        }



    }
}

