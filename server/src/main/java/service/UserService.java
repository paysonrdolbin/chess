package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

public class UserService {
    public RegisterResponse register(RegisterRequest request){
        UserData user = new UserData(request.getUsername(), request.getPassword(), request.getEmail());
        UserDAO.add(user);
        AuthData userAuthData = AuthDAO.add(user.getUsername());
        RegisterResponse response = new RegisterResponse(userAuthData);
        return response;
    }

    public LoginResponse login(LoginRequest request){
        UserData user = UserDAO.getUserData(request.getUsername());
        if (request.getPassword().equals(user.getPassword())) {
            AuthData authData = AuthDAO.add(user.getUsername());
            LoginResponse response = new LoginResponse(authData);
            return response;
        } else {
            throw new IllegalArgumentException("Wrong password");
        }
    }

    public void logout(LogoutRequest request){
        String authToken = request.getAuthToken();
        AuthDAO.delete(authToken);
    }

}
