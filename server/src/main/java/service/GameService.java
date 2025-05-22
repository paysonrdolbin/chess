package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;

import java.util.Random;

public class GameService {
    public CreateGameResponse create(CreateGameRequest request){
        AuthDAO.verify(request.getAuthToken());
        int gameID = 1111 + new Random().nextInt(9000);
        GameData gameData = new GameData(gameID, null, null, request.getGameName(), new ChessGame());
        GameDAO.create(gameData);
        return new CreateGameResponse(gameID);
    }

    public void join(JoinGameRequest request){
        AuthDAO.verify(request.getAuthToken());
        String username = AuthDAO.getUsername(request.getAuthToken());
        GameData data = GameDAO.get(request.getGameID());
        if(request.getColor() == ChessGame.TeamColor.WHITE){
            if(data.getWhiteUsername() == null){
                GameData newData = new GameData(data.getGameID(), username, data.getBlackUsername(), data.getGameName(), data.getGame());
            } else{
                throw new IllegalArgumentException("Error: already taken");
            }
        } else {
            if(data.getBlackUsername() == null){
                GameData newData = new GameData(data.getGameID(), data.getWhiteUsername(), username, data.getGameName(), data.getGame());
            } else{
                throw new IllegalArgumentException("Error: already taken");
            }
        }
    }


}
