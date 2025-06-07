package response;

import model.AuthData;

public class RegisterResponse {
    private final String username;
    private final String authToken;
    public RegisterResponse(AuthData auth){
        this.authToken = auth.getAuthToken();
        this.username = auth.getUsername();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername(){
        return username;
    }
}
