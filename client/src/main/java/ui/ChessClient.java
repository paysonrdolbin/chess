package ui;

import chess.*;
import exception.ResponseException;
import model.ListGameShortResponse;
import response.ListGamesResponse;

import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {
    private final static Scanner SCANNER = new Scanner(System.in);
    private final ServerFacade serverFacade;
    private boolean loggedIn = false;
    private boolean clientRunning = true;
    private boolean inGameplay = false;
    private ListGamesResponse gameListObject = null;
    private ArrayList<ListGameShortResponse> gameList = new ArrayList<>();
    private boolean isWhite = false;
    private int gameID = 0;

    private final String preLoginMessage = """
                Here are your options:
                
                register [username] [password] [email] - Createsan account so you can play (Also logs you in :)
                login [username] [password] - Logs you into your account.
                quit - Quits your session.
                help - See this message again.
                """;

    public ChessClient(String url){
        serverFacade = new ServerFacade(url);
    }

    public void run(){
        System.out.println("♕ Welcome to Payson's Chess Server! ♕\nType 'help' to get started.");

        while(clientRunning) {
            // pre-login
            preLogin();
            // post-login
            postLogin();
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
                    loggedIn = true;
                    postLogin();
                    break;

                case "login":
                    handleLogin(words);
                    loggedIn = true;
                    break;

                case "quit":
                    handleQuit();
                    return;
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
                    inGameplay = true;
                    gameplay();
                    break;

                case "list":
                    handleList();
                    break;

                case "observe":
                    handleObserve(words);
                    inGameplay = true;
                    gameplay();
                    break;

                case "quit":
                    handleQuit();
                    return;

                case "logout":
                    handleLogout();
                    loggedIn = false;
                    break;
            }
        }
    }

    public void gameplay(){
        while(inGameplay){
            System.out.println(">>> ");
            String input = SCANNER.nextLine();
            String[] words = input.trim().split("\\s+");

            switch(words[1].toLowerCase()){
                case "help":
                    handleGameplayHelp();
                    break;
                case "redraw":
                    ChessGame game = new ChessGame(); // replace with getting the chessboard from the server.
                    ChessBoardUI.main(game, isWhite, null);
                    break;
                case "highlight":
                    ChessGame game1 = new ChessGame(); // use the current board?? get board?? -- websocket
                    ChessPosition coord = decodePosition(words[2]);
                    ChessBoardUI.main(game1, isWhite, coord);
                    break;
                case "move":
                    handleMove(words);
                    break;
            }


        }

    }




    private ChessPosition decodePosition(String pos){
        int row = Integer.parseInt(pos.substring(1));
        int col;
        switch(pos.charAt(0)){
            case 'a': col = 1; break;
            case 'b': col = 2; break;
            case 'c': col = 3; break;
            case 'd': col = 4; break;
            case 'e': col = 5; break;
            case 'f': col = 6; break;
            case 'g': col = 7; break;
            case 'h': col = 8; break;
            default: col = -1;
        }
        return new ChessPosition(row, col);
    }

    private void handleMove(String[] words){
        try {
            ChessGame game1 = new ChessGame(); // use the current game from websocket
            ChessBoard board = game1.getBoard();
            ChessPosition startPos = decodePosition(words[1]);
            ChessPosition endPos = decodePosition(words[2]);
            ChessMove move;
            // deal with promotion pieces

            // if the piece type is a pawn
            if (board.getPiece(startPos).equals(ChessPiece.PieceType.PAWN)) {
                // if the end position is on the respective final row
                if ((endPos.getRow() == 8 && isWhite == true) || (endPos.getRow() == 1 && isWhite == false)) {
                    // prompts the user for a promotion piece
                    System.out.println("Please choose a new piece type for your pawn");
                    System.out.println(">>> ");
                    String input = SCANNER.nextLine();
                    String[] words1 = input.trim().split("\\s");
                    ChessPiece.PieceType promotion = decodePieceType(words1[0].toLowerCase());
                    // they didn't give a right piece type -- provide options???
                    if (promotion.equals(null)) {
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
            game1.makeMove(move);
            // websocket update board with new move.
            ChessBoardUI.main(game1, isWhite, null);
        } catch(InvalidMoveException e){
            System.out.println("Please provide a valid chess move in a similar format 'e4g6'.");
            System.out.println("Use 'highlight' to see valid piece moves.");
        }
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
        move [chessmove] - Excecutes the given chess move if valid. (Chess move notation is required. ex: 'e4g6')
        resign - Forfeit the current game. 
        highlight [chess position] - Highlights all the valid moves for the piece at the given position.
        help - See this message again.
        """);
    }


    private void handleResignation(){
        System.out.println("You have resigned. You forfeit this match.");
        // update the game board?
    }

    private void handlePreLoginHelp() {
        System.out.println(preLoginMessage);
    }

    private void handleRegister(String[] words) {
        if(words.length > 3) {
            try {
                serverFacade.register(words[1], words[2], words[3]);
                System.out.println("Registered! You are now logged in.");
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
            if(gameListObject != null && !gameList.isEmpty()) {
                // if an ID has been provided, observe the game.
                try{
                    int gameIndex = Integer.parseInt(words[1]) - 1;
                    if(gameIndex >= gameList.size() || gameIndex < 0){
                        System.out.println("Please enter a valid game ID.");
                    } else {
                        gameID = gameList.get(gameIndex).getGameID();
                        if(words[2].toLowerCase().equals("white")){
                            isWhite = true;
                        } else if(words[2].toLowerCase().equals("black")){
                            isWhite = false;
                        } else{
                            throw new IllegalArgumentException("Error: bad request");
                        }
                        serverFacade.join(gameID, words[2]);
                        System.out.println("Game joined!");
                        ChessGame game = new ChessGame(); // use websocket to get the game?
                        ChessBoardUI.main(game, isWhite, null);
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
            } // if the user has made a list request, but there aren't any current games.
            else if (gameListObject != null && gameList.isEmpty()){
                // if there are no current games.
                System.out.println("There are no current games. Please create a game to begin");
            } // if the user hasn't made a list request, list the games.
            else {
                // if the games haven't been listed, provide a list.
                gameListObject = list(gameListObject, gameList);
                gameList = gameListObject.getGames();
                System.out.println("Please type 'join' followed by the game ID number and white/black.");
            }
        } else {
            // no ID provided, list the games and give instructions.
            gameListObject = list(gameListObject, gameList);
            gameList = gameListObject.getGames();
            System.out.println("Please type 'join' followed by the game ID number and white/black.");
        }
    }

    private void handleList() {
        gameListObject = list(gameListObject, gameList);
        gameList = gameListObject.getGames();
    }

    private void handleObserve(String[] words) {
        if(words.length > 1) {
            if(gameListObject != null && !gameList.isEmpty()) {
                // if an ID has been provided, join the game.
                try {
                    int gameIndex = Integer.parseInt(words[1]) - 1;
                    gameID = gameList.get(gameIndex).getGameID();
                    Boolean isWhite = true;
                    serverFacade.observe(gameID);
                    System.out.println("You are now observing!");
                    ChessGame game = new ChessGame();
                    ChessBoardUI.main(game, isWhite, null);
                } catch (Exception e) {
                    System.out.println("Please type 'observe', followed by the game ID number.");
                }
            } else if (gameListObject != null && gameList.isEmpty()){
                // if there are no current games.
                System.out.println("There are no current games. Please create a game to begin");
            } else {
                // if the games haven't been listed, provide a list.
                gameListObject = list(gameListObject, gameList);
                gameList = gameListObject.getGames();
                System.out.println("Please type 'observe' followed by the game ID number.");
            }
        } else {
            // no ID provided
            gameListObject = list(gameListObject, gameList);
            gameList = gameListObject.getGames();
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

    public ListGamesResponse list(ListGamesResponse listResponse, ArrayList<ListGameShortResponse> games){
        try {
            listResponse = serverFacade.list();
            games = listResponse.getGames();
            if(!games.isEmpty()){
                System.out.println("Here are the current games:");
                int n = 1;
                while (n <= games.size()) {
                    ListGameShortResponse game = games.get(n - 1);
                    String whiteUsername;
                    if(game.getWhiteUsername() == null){
                        whiteUsername = "available";
                    } else{
                        whiteUsername = game.getWhiteUsername();
                    } String blackUsername;
                    if(game.getBlackUsername() == null){
                        blackUsername = "available";
                    } else{
                        blackUsername = game.getBlackUsername();
                    }
                    String statement = n + ". " + game.getGameName() + ", ID: " + game.getGameID() + " - White: "
                            + whiteUsername + " Black: " + blackUsername;
                    System.out.println(statement);
                    n++;
                }
            } else {
                System.out.println("There are no current games. Please create a game to begin.");
            }

        } catch(ResponseException e){
            if(e.statusCode() == 400){
                System.out.println("Server Error");
            } else{
                System.out.println(e.getMessage());
            }
        }
        return listResponse;
    }

}
