package handler;

import request.RegisterRequest;
import response.RegisterResponse;
import service.RegisterService;
import spark.Route;
import spark.Request;
import spark.Response;


public class RegisterHandler implements Route {
    public RegisterHandler(){}
    @Override
    public Object handle(Request req, Response res){
        public Object handle(Request req, Response res) throws Exception{
            RegisterService service = new RegisterService();
            RegisterRequest registerRequest = new RegisterRequest();
            RegisterResponse servResponse = service.register(registerRequest);
            res.status(200);
            return "{}";
        }
    }
}
