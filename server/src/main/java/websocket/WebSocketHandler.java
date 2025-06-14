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
import java.util.Set;

import static utils.MoveNotation.posToString;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String type = json.get("commandType").getAsString();
        UserGameCommand command;
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

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            int gameID = command.getGameID();
            AuthDAO.verify(command.getAuthToken());
            String username = AuthDAO.getUsername(command.getAuthToken());
            sessions.addSessionToGame(command.getGameID(), session);
            GameData data = GameDAO.get(gameID);
            ChessGame game = data.getGame();
            String statusMessage;
            if(data.getWhiteUsername() != null && data.getWhiteUsername().equals(username)){
                statusMessage = "white";
            } else if(data.getBlackUsername() != null && data.getBlackUsername().equals(username)){
                statusMessage = "black";
            } else {
                statusMessage = "an observer";
            }
            ServerMessage.LoadGameMessage loadMessage = new ServerMessage.LoadGameMessage(game);
            String msg = new Gson().toJson(loadMessage);
            broadcastRootUser(session, msg);

            String entryMessage = username + " joined as " + statusMessage;
            ServerMessage.Notification entryNote = new ServerMessage.Notification(entryMessage);
            String entryMsg = new Gson().toJson(entryNote);
            broadcastAllOtherUsers(gameID, session, entryMsg);

        } catch (Exception e) {
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage("Internal Error. Please try again.");
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    private void makeMove(UserGameCommand.MakeMoveCommand command, Session session) throws IOException {
        try {
            int gameID = command.getGameID();
            GameData data = GameDAO.get(gameID);
            ChessGame game = data.getGame();
            ChessGame.TeamColor color = null;

            // checks to make sure the game is over before allowing moves first.
            if(game.isGameOver()){
                throw new Exception("Game is over. No new moves can be made.");
            }
            String username = AuthDAO.getUsername(command.getAuthToken());
            ChessMove move = command.getMove();

            if(data.getWhiteUsername() != null && data.getWhiteUsername().equals(username)){
                color = ChessGame.TeamColor.WHITE;
            } else if(data.getBlackUsername() != null && data.getBlackUsername().equals(username)){
                color = ChessGame.TeamColor.BLACK;
            }
            if(color == null){
                throw new Exception("You are observing and cannot make a move in this game.");
            }
            if(game.getTeamTurn() != color){
                throw new Exception("A move cannot be made during your opponent's turn.");
            }


            game.makeMove(move);
            GameData updatedData = new GameData(gameID, data.getWhiteUsername(), data.getBlackUsername(), data.getGameName(), game);
            GameDAO.update(updatedData);

            ServerMessage.LoadGameMessage message = new ServerMessage.LoadGameMessage(game);
            String loadMsg = new Gson().toJson(message);
            broadcastAllUsers(gameID, loadMsg);
            // broadcast message to all
            ServerMessage.Notification notification = new ServerMessage.Notification(
                    username + " has made the move " +
                             posToString(move.getStartPosition()) + " " + posToString(move.getEndPosition()));
            String moveNote = new Gson().toJson(notification);
            broadcastAllOtherUsers(gameID, session, moveNote);

            // checks if the game is in check, checkmate, or stalemate.
            if(game.isInCheckmate(color)) {
                ServerMessage.Notification checkmateNote = new ServerMessage.Notification(color + "is in checkmate! Game over.");
                String msg = new Gson().toJson(checkmateNote);
                broadcastAllUsers(gameID, msg);
            } else if(game.isInCheck(color)){
                ServerMessage.Notification checkNote = new ServerMessage.Notification(color + " is in check!");
                String msg = new Gson().toJson(checkNote);
                broadcastAllUsers(gameID, msg);
            } else if(game.isInStalemate(color)) {
                ServerMessage.Notification staleNote = new ServerMessage.Notification("Stalemate! Game over.");
                String msg = new Gson().toJson(staleNote);
                broadcastAllUsers(gameID, msg);
            }
        } catch (InvalidMoveException e) {
            ServerMessage.ErrorMessage message = new ServerMessage.ErrorMessage(e.getMessage());
            String msg = new Gson().toJson(message);
            broadcastRootUser(session, msg);
        } catch(Exception e){
            ServerMessage.ErrorMessage message = new ServerMessage.ErrorMessage(e.getMessage());
            String msg = new Gson().toJson(message);
            broadcastRootUser(session, msg);
        }
    }

    private void leaveGame(UserGameCommand command, Session session) throws IOException {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            String username = AuthDAO.getUsername(authToken);
            GameData data = GameDAO.get(gameID);

            if(data.getWhiteUsername() != null && data.getWhiteUsername().equals(username)){
                GameData updatedGame = new GameData(gameID, null, data.getBlackUsername(), data.getGameName(), data.getGame());
                GameDAO.update(updatedGame);
            } else if(data.getBlackUsername() != null && data.getBlackUsername().equals(username)){
                GameData updatedGame = new GameData(gameID, data.getWhiteUsername(), null, data.getGameName(), data.getGame());
                GameDAO.update(updatedGame);
            }

            sessions.removeSessionFromGame(command.getGameID(), session);
            session.close();
            ServerMessage.Notification exitNote = new ServerMessage.Notification(username + " has left the game.");
            String msg = new Gson().toJson(exitNote);
            broadcastAllUsers(gameID, msg);

        } catch(DataAccessException e){
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage("Internal Error");
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    private void resignGame(UserGameCommand command, Session session) throws IOException {
        try {

            int gameID = command.getGameID();
            String authToken = command.getAuthToken();
            String username = AuthDAO.getUsername(authToken);
            GameData data = GameDAO.get(gameID);
            String white = data.getWhiteUsername();
            String black = data.getBlackUsername();
            ChessGame game = data.getGame();
            if(game.isGameOver()){
                throw new Exception("Game is already over. You cannot resign now.");
            }

            if (!username.equals(white) && !username.equals(black)) {
                throw new Exception("You are observing, you may not resign.");
            }

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
        } catch(Exception e){
            ServerMessage.ErrorMessage errorMessage = new ServerMessage.ErrorMessage(e.getMessage());
            String msg = new Gson().toJson(errorMessage);
            broadcastRootUser(session, msg);
        }
    }

    public void broadcastAllUsers(int gameID, String message) throws IOException{
            Set<Session> allSessions = sessions.getSessionsForGame(gameID);
            for (Session session: allSessions){
                session.getRemote().sendString(message);
            }
    }

    public void broadcastAllOtherUsers(int gameID, Session rootSession, String message) throws IOException {
            Set<Session> allSessions = sessions.getSessionsForGame(gameID);
            for(Session session: allSessions){
                if(!session.equals(rootSession)) {
                    session.getRemote().sendString(message);
                }
            }
    }

    public void broadcastRootUser(Session rootSession, String message) throws IOException {
        rootSession.getRemote().sendString(message);
    }
}
