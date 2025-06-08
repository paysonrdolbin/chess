import chess.*;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        ChessClient client = new ChessClient("http://localhost:8080");
        client.run();
    }
}