package handler;

import model.UserData;
import com.google.gson.Gson;
import request.RegisterRequest;
import response.RegisterResponse;
import service.UserService;
import spark.Route;
import spark.Request;
import spark.Response;


public class RegisterHandler implements Route {
    // default constructor
    private final Gson gson = new Gson();

    public RegisterHandler() {
    }
    @Override
    public Object handle(Request req, Response res){
        UserData user = gson.fromJson(req.body(), UserData.class);

        UserService service = new UserService();
        RegisterRequest registerRequest = new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());
        RegisterResponse servResponse = service.register(registerRequest);
        res.status(200);
        return gson.toJson(servResponse);


    }
}
