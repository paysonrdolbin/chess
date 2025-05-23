package handler;

import chess.InvalidMoveException;
import model.UserData;
import com.google.gson.Gson;
import request.RegisterRequest;
import response.RegisterResponse;
import service.ErrorService;
import service.UserService;
import spark.Route;
import spark.Request;
import spark.Response;
import java.util.Map;


public class RegisterHandler implements Route {
    // default constructor
    private final Gson gson = new Gson();

    public RegisterHandler() {
    }
    @Override
    public Object handle(Request req, Response res){
        UserData user = gson.fromJson(req.body(), UserData.class);
        try{
            UserService service = new UserService();
            RegisterRequest registerRequest = new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());
            RegisterResponse servResponse = service.register(registerRequest);
            res.status(200);
            return gson.toJson(servResponse);
        } catch (IllegalArgumentException e){
            res.status(ErrorService.errorCode(e.getMessage()));
            return gson.toJson(Map.of("message",e.getMessage()));
        }

    }
}
