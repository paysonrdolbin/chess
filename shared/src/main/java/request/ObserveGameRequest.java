package request;

public class ObserveGameRequest {
    private final int gameID;
    private final String authToken;

    public ObserveGameRequest(String authToken, int gameID){
        this.authToken = authToken;
        this.gameID = gameID;
    }


    public int getGameID() {
        return gameID;
    }

    public String getAuthToken() {
        return authToken;
    }
}
