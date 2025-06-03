package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

public class UserService {
    public RegisterResponse register(RegisterRequest request) throws DataAccessException {
        if(request.getUsername() == null || request.getPassword() == null || request.getEmail() == null){
            throw new IllegalArgumentException("Error: bad request");
        }
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        UserData user = new UserData(request.getUsername(), hashedPassword, request.getEmail());
        UserDAO.add(user);
        AuthData userAuthData = AuthDAO.add(user.getUsername());
        RegisterResponse response = new RegisterResponse(userAuthData);
        return response;
    }

    public LoginResponse login(LoginRequest request) throws DataAccessException{
        // if either field is empty, throw bad request
        if(request.getUsername() == null || request.getPassword() == null){
            throw new IllegalArgumentException("Error: bad request");
        }
//        UserDAO.exist(request.getUsername());
        UserData user = UserDAO.getUserData(request.getUsername());
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            AuthData authData = AuthDAO.add(user.getUsername());
            LoginResponse response = new LoginResponse(authData);
            return response;
        }
        else {
            throw new IllegalArgumentException("Error: unauthorized");
        }
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        String authToken = request.getAuthToken();
        AuthDAO.delete(authToken);
    }




}
