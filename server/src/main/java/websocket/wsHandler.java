package websocket;

import chess.*;
import com.google.gson.*;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static utils.MoveNotation.posToString;

@WebSocket
public class wsHandler {
    WebSocketSessions sessions = new WebSocketSessions();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String type = json.get("commandType").getAsString();
        UserGameCommand command = null;
        switch (type) {
            case "CONNECT":
                command = new Gson().fromJson(message, UserGameCommand.class);
                connect(command, session);
                break;
            case "MAKE_MOVE":
                UserGameCommand.MakeMoveCommand moveCommand = new Gson().fromJson(message, UserGameCommand.MakeMoveCommand.class);
                makeMove(moveCommand, session);
                break;
            case "LEAVE":
                command = new Gson().fromJson(message, UserGameCommand.class);
                leaveGame(command, session);
                break;
            case "RESIGN":
                command = new Gson().fromJson(message, UserGameCommand.class);
                resignGame(command, session);
                break;
        }
    }

    private void connect(UserGameCommand command, Session session){
        try {
            String status = "white";
            int gameID = command.getGameID();
            sessions.addSessionToGame(command.getGameID(), session);
            ChessGame game = GameDAO.get(gameID).getGame();
            ServerMessage.LoadGameMessage loadMessage = new ServerMessage.LoadGameMessage(game);
            String msg = new Gson().toJson(loadMessage);
            broadcastRootUser(session, msg);

            // notify everyone
            String statusMessage;
            switch(status){
                case "white":
                    statusMessage = "white";
                    break;
                case "black":
                    statusMessage = "black";
                    break;
                case "observe":
                    statusMessage = "an observer";
                    break;
                default:
                    statusMessage = "a player";
            }
            ServerMessage.Notification entryNote = new ServerMessage.Notification(statusMessage);
            String entryMsg = new Gson().toJson(entryNote);
            broadcastAllOtherUsers(gameID, session, entryMsg);

        } catch (DataAccessException e) {
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage("Internal Error. Please try again.");
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    private void makeMove(UserGameCommand.MakeMoveCommand command, Session session){
        try {
            int gameID = command.getGameID();
            ChessGame game = GameDAO.get(gameID).getGame();

            // checks to make sure the game is over before allowing moves first.
            if(game.isGameOver()){
                throw new Exception("Game is over. No new moves can be made.");
            }
            String username = AuthDAO.getUsername(command.getAuthToken());
            ChessMove move = command.getMove();

            String color = "color"; // figure this part out!!

            game.makeMove(move);
            ServerMessage.LoadGameMessage message = new ServerMessage.LoadGameMessage(game);
            // broadcast message to all
            ServerMessage.Notification notification = new ServerMessage.Notification(
                    username + " has made the move " +
                             posToString(move.getStartPosition()) + " " + posToString(move.getEndPosition()));
            // broadcast to all other users

            // checks if the game is in check, checkmate, or stalemate.
            if(game.isInCheckmate()) {
                ServerMessage.Notification checkmateNote = new ServerMessage.Notification(color + "is in checkmate! Game over.");
                String msg = new Gson().toJson(checkmateNote);
                broadcastAllUsers(gameID, msg);
            } else if(game.isInCheck()){
                ServerMessage.Notification checkNote = new ServerMessage.Notification(color + " is in check!");
                String msg = new Gson().toJson(checkNote);
                broadcastAllUsers(gameID, msg);
            } else if(game.isInStalemate()) {
                ServerMessage.Notification staleNote = new ServerMessage.Notification("Stalemate! Game over.");
                String msg = new Gson().toJson(message);
                broadcastAllUsers(gameID, msg);
            }
        } catch (InvalidMoveException e) {
            ServerMessage.ErrorMessage message = new ServerMessage.ErrorMessage("This is an invalid move.");
            String msg = new Gson().toJson(message);
            broadcastRootUser(session, msg);
        } catch(Exception e){
            ServerMessage.ErrorMessage message = new ServerMessage.ErrorMessage(e.getMessage());
            String msg = new Gson().toJson(message);
            broadcastRootUser(session, msg);
        }
    }

    private void leaveGame(UserGameCommand command, Session session){
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            String username = AuthDAO.getUsername(authToken);
            GameData game = GameDAO.get(gameID);
            // figure out how keep track of the session's status as an observer or as a player.
            GameData updatedGame = new GameData(gameID, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
            GameDAO.update(updatedGame);
            sessions.removeSessionFromGame(command.getGameID(), session);
            ServerMessage.Notification exitNote = new ServerMessage.Notification(username + " has left the game.");
            String msg = new Gson().toJson(exitNote);
            broadcastAllUsers(gameID, msg);

        } catch(DataAccessException e){
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage("Internal Error");
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    private void resignGame(UserGameCommand command, Session session){
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            String username = AuthDAO.getUsername(authToken);
            GameData data = GameDAO.get(gameID);
            ChessGame game = data.getGame();
            game.setGameOver(true);
            GameData updatedData = new GameData(data.getGameID(), data.getWhiteUsername(), data.getBlackUsername(), data.getGameName(), game);
            GameDAO.update(updatedData);
            ServerMessage.Notification resignNote = new ServerMessage.Notification(username + " has resigned. Game over.");
            String msg = new Gson().toJson(resignNote);
            broadcastAllUsers(gameID, msg);

        } catch(DataAccessException e){
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage("Internal Error");
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    public void broadcastAllUsers(int gameID, String message){
        try{
            Set<Session> allSessions = sessions.getSessionsForGame(gameID);
            for (Session session: allSessions){
                session.getRemote().sendString(message);
            }
        } catch(IOException e){
            // figure out what to do here...
        }
    }

    public void broadcastAllOtherUsers(int gameID, Session rootSession, String message){
        try{
            Set<Session> allSessions = sessions.getSessionsForGame(gameID);
            for(Session session: allSessions){
                if(!session.equals(rootSession)) {
                    session.getRemote().sendString(message);
                }
            }

        } catch(IOException e){
            // figure out what to do here as well
        }
    }

    public void broadcastRootUser(Session rootSession, String message){
        try{
            rootSession.getRemote().sendString(message);
        } catch (IOException e) {

        }
    }


}
