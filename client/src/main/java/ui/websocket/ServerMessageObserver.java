package ui.websocket;

import ui.ChessClient;
import websocket.messages.ServerMessage;

public class ServerMessageObserver {
    ChessClient client;

    public ServerMessageObserver(ChessClient client) {
        this.client = client;
    }

    public void displayGame(ServerMessage.LoadGameMessage loadGameMessage){
        client.printGame(loadGameMessage.getGame());
    }

    public void displayError(ServerMessage.ErrorMessage errorMessage){
        client.printError(errorMessage.getMessage());
    }

    public void displayNotification(ServerMessage.Notification notification){
        client.printNotification(notification.getMessage());
    }
}
