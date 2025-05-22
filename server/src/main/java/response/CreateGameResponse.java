package response;

public class CreateGameResponse {
    private final int gameID;

    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
