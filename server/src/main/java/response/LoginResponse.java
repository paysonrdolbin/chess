package response;

import model.AuthData;

public class LoginResponse {
    private final AuthData data;
    public LoginResponse(AuthData data){
        this.data = data;
    }
}
