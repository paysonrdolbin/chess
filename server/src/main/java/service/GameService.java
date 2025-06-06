package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.ListGameShortResponse;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

import java.util.ArrayList;
import java.util.Random;

public class GameService {
    public CreateGameResponse create(CreateGameRequest request) throws DataAccessException{
        AuthDAO.verify(request.getAuthToken());
        int gameID = 1111 + new Random().nextInt(9000);
        GameData gameData = new GameData(gameID, null, null, request.getGameName(), new ChessGame());
        GameDAO.create(gameData);
        return new CreateGameResponse(gameID);
    }

    public void join(JoinGameRequest request) throws DataAccessException {
        AuthDAO.verify(request.getAuthToken());
        String username = AuthDAO.getUsername(request.getAuthToken());
        GameData data = GameDAO.get(request.getGameID());
        GameData newData;
        if(request.getColor() == ChessGame.TeamColor.WHITE){
            if(data.getWhiteUsername() == null){
                newData = new GameData(data.getGameID(), username, data.getBlackUsername(), data.getGameName(), data.getGame());
            } else{
                throw new IllegalArgumentException("Error: already taken");
            }
        } else {
            if(data.getBlackUsername() == null){
                newData = new GameData(data.getGameID(), data.getWhiteUsername(), username, data.getGameName(), data.getGame());
            } else{
                throw new IllegalArgumentException("Error: already taken");
            }
        }
        GameDAO.update(newData);
    }

    public ListGamesResponse list(ListGamesRequest request) throws DataAccessException {
        AuthDAO.verify(request.getAuthToken());
        ArrayList<GameData> allGameData = GameDAO.list();
        ArrayList<ListGameShortResponse> allGameDetails = new ArrayList<>();
        for(GameData game: allGameData){
            ListGameShortResponse gameDetails =
                    new ListGameShortResponse(
                            game.getGameID(),
                            game.getWhiteUsername(),
                            game.getBlackUsername(),
                            game.getGameName(),
                            game.getGame());
            allGameDetails.add(gameDetails);
        }
        return new ListGamesResponse(allGameDetails);
    }
}
