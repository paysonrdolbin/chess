package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import response.RegisterResponse;

public class UserService {
    public RegisterResponse register(RegisterRequest request){
        UserData user = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
        UserDAO.add(user);
        AuthData userAuthData = AuthDAO.add(user.getUsername());
        RegisterResponse response = new RegisterResponse(userAuthData);
        return response;
    }
}
