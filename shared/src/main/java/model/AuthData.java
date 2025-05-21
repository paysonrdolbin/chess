package model;

public class AuthData {
    String username;
    String authToken;

    public AuthData(String username, String authToken) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
