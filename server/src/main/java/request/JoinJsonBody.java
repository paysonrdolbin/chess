package request;

import chess.ChessGame;

public class JoinJsonBody {
    private final ChessGame.TeamColor color;
    private final int gameID;

    public JoinJsonBody(ChessGame.TeamColor color, int gameID) {
        this.color = color;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public int getGameID() {
        return gameID;
    }
}
