package response;

import model.AuthData;

public class LoginResponse {
    private final String username;
    private final String authToken;
    public LoginResponse(AuthData data){
        this.username = data.getUsername();
        this.authToken = data.getAuthToken();
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
