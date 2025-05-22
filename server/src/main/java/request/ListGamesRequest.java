package request;

public class ListGamesRequest {
    private final String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public ListGamesRequest(String authToken) {
        this.authToken = authToken;
    }
}
