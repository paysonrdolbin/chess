package ui;

import chess.*;
import exception.ResponseException;
import model.ListGameShortResponse;
import response.ListGamesResponse;
import ui.websocket.ServerMessageObserver;
import ui.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Scanner;

import static utils.MoveNotation.stringToPos;

public class ChessClient {
    private final static Scanner SCANNER = new Scanner(System.in);
    private final ServerFacade serverFacade;
    private boolean loggedIn = false;
    private boolean clientRunning = true;
    private boolean inGameplay = false;
    private ArrayList<ListGameShortResponse> gameList = new ArrayList<>();
    private boolean isWhite = false;
    private int gameID = 0;
    private String authToken;
    private ChessGame latestGame =  null;
    private String url;

    private final String preLoginMessage = """
                Here are your options:
                
                register [username] [password] [email] - Creates an account so you can play (Also logs you in :)
                login [username] [password] - Logs you into your account.
                quit - Quits your session.
                help - See this message again.
                """;

    public ChessClient(String url){
        serverFacade = new ServerFacade(url);
        this.url = url;
    }

    public void run(){
        System.out.println("♕ Welcome to Payson's Chess Server! ♕\nType 'help' to get started.");

        while(clientRunning) {
            // pre-login
            preLogin();
        }
    }

    public void preLogin(){
        while (!loggedIn) {
            System.out.println(">>> ");
            String input = SCANNER.nextLine();
            String[] words = input.trim().split("\\s+");

            switch (words[0].toLowerCase()) {
                case "help":
                    handlePreLoginHelp();
                    break;

                case "register":
                    handleRegister(words);
                    break;

                case "login":
                    handleLogin(words);
                    break;

                case "quit":
                    handleQuit();
                    return;
                default:
                    System.out.println("Please give a proper command.");
                    handlePreLoginHelp();
            }
        }
    }

    public void postLogin(){
        while (loggedIn) {
            System.out.println(">>> ");
            String input = SCANNER.nextLine();
            String[] words = input.trim().split("\\s+");

            switch (words[0].toLowerCase()) {

                case "help":
                    handlePostLoginHelp();
                    break;

                case "create":
                    handleCreate(words);
                    break;

                case "join":
                    handleJoin(words);
                    break;

                case "list":
                    handleList();
                    break;

                case "observe":
                    handleObserve(words);
                    break;

                case "quit":
                    handleQuit();
                    return;

                case "logout":
                    handleLogout();
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Please give a proper command.");
                    handlePostLoginHelp();
            }
        }
    }

    public void gameplay(){
        WebSocketFacade wsFacade = null;
        try {
            ServerMessageObserver smo = new ServerMessageObserver(this);
            wsFacade = new WebSocketFacade(url, smo, gameID, authToken);
            wsFacade.connectGame();
        } catch(ResponseException e){
            System.out.println("System error.");
            inGameplay = false;
        }
        while (inGameplay) {
            try {
                System.out.println(">>> ");
                String input = SCANNER.nextLine();
                String[] words = input.trim().split("\\s+");

                switch (words[0].toLowerCase()) {
                    case "help":
                        handleGameplayHelp();
                        break;
                    case "redraw":
                        ChessBoardUI.main(latestGame, isWhite, null);
                        break;
                    case "highlight":
                        ChessPosition coord = stringToPos(words[1]);
                        ChessBoardUI.main(latestGame, isWhite, coord);
                        break;
                    case "move":
                        ChessMove move = handleMove(words);
                        wsFacade.makeMove(move);
                        break;
                    case "leave":
                        wsFacade.leaveGame();
                        inGameplay = false;
                        break;
                    case "resign":
                        wsFacade.resignGame();
                        break;
                    default:
                        System.out.println("Please give a proper command.");
                        handleGameplayHelp();
                }
            } catch(ResponseException e){
                System.out.println(e.getMessage());
            } catch(Exception e){
                System.out.println("Check your parameters and try again.");
            }

        }
    }

    private ChessMove handleMove(String[] words){
            ChessBoard board = latestGame.getBoard();
            ChessPosition startPos = stringToPos(words[1]);
            ChessPosition endPos = stringToPos(words[2]);
            ChessMove move;
            // deal with promotion pieces

            // if the piece type is a pawn
            ChessPiece piece = board.getPiece(startPos);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                // if the end position is on the respective final row
                if ((endPos.getRow() == 8 && isWhite == true) || (endPos.getRow() == 1 && isWhite == false)) {
                    // prompts the user for a promotion piece
                    System.out.println("Please choose a new piece type for your pawn");
                    System.out.println(">>> ");
                    String input = SCANNER.nextLine();
                    String[] words1 = input.trim().split("\\s");
                    ChessPiece.PieceType promotion = decodePieceType(words1[0].toLowerCase());
                    // they didn't give a right piece type -- provide options???
                    if (promotion == null) {
                        throw new IllegalArgumentException("Please provide a valid piece for promotion."); // fix this
                    }
                    move = new ChessMove(startPos, endPos, promotion);
                } // otherwise promotion = null
                else {
                    move = new ChessMove(startPos, endPos, null);
                } // otherwise promotion = null
            } else {
                move = new ChessMove(startPos, endPos, null);
            }

            return move;
    }

    private ChessPiece.PieceType decodePieceType(String input){
        ChessPiece.PieceType promotion = null;
        switch(input){
            case "queen":
                promotion = ChessPiece.PieceType.QUEEN;
                break;
            case "bishop":
                promotion = ChessPiece.PieceType.BISHOP;
                break;
            case "knight":
                promotion = ChessPiece.PieceType.KNIGHT;
                break;
            case "rook":
                promotion = ChessPiece.PieceType.ROOK;
                break;
        }
        return promotion;
    }


    private void handleGameplayHelp(){
        System.out.println("""
        Here are your options:
        
        redraw - Draws an updated chess board. 
        leave - Removes you from the current game. 
        move [chessmove] - Executes the given chess move if valid. (Chess move notation is required. ex: 'e4 g6')
        resign - Forfeit the current game. 
        highlight [chess position] - Highlights all the valid moves for the piece at the given position.
        help - See this message again.
        """);
    }


    private void handlePreLoginHelp() {
        System.out.println(preLoginMessage);
    }

    private void handleRegister(String[] words) {
        if(words.length > 3) {
            try {
                serverFacade.register(words[1], words[2], words[3]);
                System.out.println("Registered! You are now logged in.");
                loggedIn = true;
                authToken = serverFacade.getAuthToken();
                postLogin();
            } catch (ResponseException e) {
                switch(e.statusCode()){
                    case 400:
                        System.out.println("Check all fields are filled properly and try again.");
                        break;
                    case 403:
                        System.out.println("Username already taken. Try again.");
                        break;
                    case 500:
                        System.out.println(e.getMessage());
                        break;
                }
            }
        } else {
            System.out.println("Please provide a username, password, and email.");
        }
    }

    private void handleLogin(String[] words) {
        if(words.length > 2) {
            try {
                serverFacade.login(words[1], words[2]);
                System.out.println("Login Success.");
                loggedIn = true;
                authToken = serverFacade.getAuthToken();
                postLogin();
            } catch (ResponseException e) {
                switch(e.statusCode()){
                    case 400:
                        System.out.println("Username does not exist.");
                        break;
                    case 401:
                        System.out.println("Username or password is incorrect.");
                        break;
                    case 500:
                        System.out.println(e.getMessage());
                        break;
                }
            }
        } else{
            System.out.println("Please provide both a username and password");
        }
    }

    private void handleQuit() {
        System.out.println("See ya!");
        clientRunning = false;
    }

    private void handlePostLoginHelp() {
        System.out.println("""
                Here are your options:
                
                create [name] - Creates a fresh new game for you to play. 
                join [ID] [white|black] - Join a game using its ID as one of the available colors. 
                list - Lists all the games currently going on in the server.
                observe - Watch a game currently going on. 
                quit - Quit from your current game. 
                logout - Quits your session. (Don't worry, your games in progress are saved.)
                help - See this message again. 
                """);
    }

    private void handleCreate(String[] words) {
        if (words.length > 1) {
            try{
                serverFacade.create(words[1]);
                System.out.println("Game '" + words[1] + "' created");
            } catch (ResponseException e) {
                switch(e.statusCode()){
                    case 400:
                        System.out.println("Bad request. Check the fields and try again.");
                        break;
                    case 401:
                        System.out.println("Sever error");
                    case 500:
                        System.out.println(e.getMessage());
                        break;
                }
            }
        } else{
            System.out.println("Please provide a game name.");
        }
    }

    private void handleJoin(String[] words) {
        // if the user provided an ID # and a team color
        if(words.length > 2) {
            // if the user has made a list request, and the list wasn't empty
            if(!gameList.isEmpty()) {
                // if an ID has been provided, observe the game.
                try{
                    int gameIndex = Integer.parseInt(words[1]) - 1;
                    if(gameIndex >= gameList.size() || gameIndex < 0){
                        System.out.println("Please enter a valid game ID.");
                    } else {
                        gameID = gameList.get(gameIndex).getGameID();
                        String color = words[2].toLowerCase();
                        if (!color.equals("white") && !color.equals("black")) {
                            throw new IllegalArgumentException("Error: bad request");
                        }
                        isWhite = color.equals("white");
                        serverFacade.join(gameID, words[2]);
                        System.out.println("Game joined!");
                        inGameplay = true;
                        gameplay();
                    }
                } catch (ResponseException e) {
                    switch(e.statusCode()){
                        case 400:
                            System.out.println("Check game ID and team color and try again.");
                            break;
                        case 401:
                            System.out.println("System error");
                            break;
                        case 403:
                            System.out.println("Color already taken. Choose a vacant position");
                            break;
                        case 500:
                            System.out.println(e.getMessage());
                            break;
                        default:
                            System.out.println("Please type 'join' followed by the game ID number and white/black.");
                    }
                } catch (Exception e){
                    System.out.println("Please type 'join' followed by the ID number and white/black");
                }
            }
            else {
                // if the games haven't been listed, provide a list.
                try {
                    gameList = listGames();
                } catch (ResponseException e) {
                    System.out.println("There was a problem fetching the list of games. Please try again.");
                    return;
                }
                System.out.println("Please type 'join' followed by the game ID number and white/black.");
            }
        } else {
            // no ID provided, list the games and give instructions.
            try {
                gameList = listGames();
            } catch (ResponseException e) {
                System.out.println("There was a problem fetching the list of games. Please try again.");
                return;
            }
            System.out.println("Please type 'join' followed by the game ID number and white/black.");
        }
    }

    private void handleList() {
        try {
            gameList = listGames();
        } catch (ResponseException e) {
            System.out.println("There was a problem fetching the list of games. Please try again.");
        }
    }

    private void handleObserve(String[] words) {
        if(words.length > 1) {
            if(!gameList.isEmpty()) {
                // if an ID has been provided, join the game.
                try {
                    int gameIndex = Integer.parseInt(words[1]) - 1;
                    gameID = gameList.get(gameIndex).getGameID();
                    System.out.println("You are now observing!");
                    inGameplay = true;
                    gameplay();
                } catch (Exception e) {
                    System.out.println("Please type 'observe', followed by the game ID number.");
                }
            } else {
                // if the games haven't been listed, provide a list.
                try {
                    gameList = listGames();
                } catch (ResponseException e) {
                    System.out.println("There was a problem fetching the list of games. Please try again.");
                    return;
                }
                System.out.println("Please type 'observe' followed by the game ID number.");
            }
        } else {
            // no ID provided
            try {
                gameList = listGames();
            } catch (ResponseException e) {
                System.out.println("There was a problem fetching the list of games. Please try again.");
                return;
            }
            System.out.println("Please type 'observe' followed by the game ID number.");
        }
    }

    private void handleLogout() {
        try{
            serverFacade.logout();
            System.out.println("Logout Successful");
        } catch (ResponseException e) {
            System.out.println("Server Error");
        }
    }

    public ArrayList<ListGameShortResponse> listGames() throws ResponseException {
        ListGamesResponse response = serverFacade.list();
        ArrayList<ListGameShortResponse> games = response.getGames();

        if (games.isEmpty()) {
            System.out.println("There are no current games. Please create a game to begin.");
        } else {
            System.out.println("Here are the current games:");
            for (int i = 0; i < games.size(); i++) {
                ListGameShortResponse game = games.get(i);
                String white = (game.getWhiteUsername() != null) ? game.getWhiteUsername() : "available";
                String black = (game.getBlackUsername() != null) ? game.getBlackUsername() : "available";
                System.out.printf("%d. %s, ID: %d - White: %s Black: %s%n", i + 1, game.getGameName(), game.getGameID(), white, black);
            }
        }

        return games;
    }

    public void printGame(ChessGame game){
        ChessBoardUI.main(game, isWhite, null);
        latestGame = game;
    }

    public void printError(String errorMessage){
        System.out.println(errorMessage);
    }

    public void printNotification(String notification){
        System.out.println(notification);
    }

}
