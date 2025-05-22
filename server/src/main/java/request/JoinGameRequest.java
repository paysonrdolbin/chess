package request;

import chess.ChessGame;

public class JoinGameRequest {
    private final String authToken;
    private final ChessGame.TeamColor color;
    private final int gameID;

    public JoinGameRequest(String authToken, ChessGame.TeamColor color, int gameID) {
        this.authToken = authToken;
        this.color = color;
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public int getGameID() {
        return gameID;
    }
}
