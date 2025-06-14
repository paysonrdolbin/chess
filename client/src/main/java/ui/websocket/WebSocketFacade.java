package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver messageHandler;
    int gameID;
    String authToken;

    public WebSocketFacade(String url, ServerMessageObserver messageHandler, int gameID, String authToken) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.gameID = gameID;

            this.authToken = authToken;

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                    String type = json.get("serverMessageType").getAsString();
                    switch (type) {
                        case "LOAD_GAME":
                            ServerMessage.LoadGameMessage loadMessage = new Gson().fromJson(message, ServerMessage.LoadGameMessage.class);
                            messageHandler.displayGame(loadMessage);
                            break;
                        case "ERROR":
                            ServerMessage.ErrorMessage errorMessage = new Gson().fromJson(message, ServerMessage.ErrorMessage.class);
                            messageHandler.displayError(errorMessage);
                            break;
                        case "NOTIFICATION":
                            ServerMessage.Notification notification = new Gson().fromJson(message, ServerMessage.Notification.class);
                            messageHandler.displayNotification(notification);
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectGame() throws ResponseException{
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(ChessMove move) throws ResponseException{
        try{
            UserGameCommand.MakeMoveCommand command = new UserGameCommand.MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leaveGame() throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resignGame() throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }



}