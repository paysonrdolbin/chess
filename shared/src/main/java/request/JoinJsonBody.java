package request;

import chess.ChessGame;

public class JoinJsonBody {
    private final String playerColor;
    private final int gameID;

    public JoinJsonBody(String color, int gameID) {
        this.playerColor = color;
        this.gameID = gameID;
    }

    public String getColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
