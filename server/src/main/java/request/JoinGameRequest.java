package request;

import chess.ChessGame;

public class JoinGameRequest {
    private final String authToken;
    private final ChessGame.TeamColor color;
    private final int gameID;

    public JoinGameRequest(String authToken, String color, int gameID) {
        this.authToken = authToken;
        if(color == null){
            throw new IllegalArgumentException("Error: bad request");
        } else if(color.equals("WHITE")){
            this.color = ChessGame.TeamColor.WHITE;
        } else if(color.equals("BLACK")){
            this.color = ChessGame.TeamColor.BLACK;
        } else{
            throw new IllegalArgumentException("Error: bad request");
        }

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
