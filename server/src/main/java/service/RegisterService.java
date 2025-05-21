package service;

import model.UserData;
import request.RegisterRequest;
import response.RegisterResponse;

public class RegisterService {
    public RegisterResponse register(RegisterRequest request){
        UserData user = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
    }
}
