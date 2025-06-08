package ui;

import exception.ResponseException;
import model.ListGameShortResponse;
import response.ListGamesResponse;

import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {
    private final Scanner Scanner = new Scanner(System.in);
    private final ServerFacade serverFacade;

    public ChessClient(String url){
        serverFacade = new ServerFacade(url);
    }

    public void run(){
        System.out.println("♕ Welcome to Payson's Chess Server! ♕\nType 'help' to get started.");
        boolean loggedIn = false;
        boolean clientRunning = true;
        ListGamesResponse gameListObject = null;
        ArrayList<ListGameShortResponse> gameList = new ArrayList<>();
        String preLoginMessage = """
                    register [username] [password] [email] - Creates an account so you can play (Also logs you in :)
                    login [username] [password] - Logs you into your account.
                    quit - Quits your session.
                    help - See this message again.
                    """;
        while(clientRunning) {
            // pre-login
            while (!loggedIn) {
                System.out.println(">>> ");
                String input = Scanner.nextLine();
                String[] words = input.trim().split("\\s+");

                switch (words[0].toLowerCase()) {
                    case "help":
                        System.out.println(preLoginMessage);
                        break;

                    case "register":
                        if(words.length > 3) {
                            try {
                                serverFacade.register(words[1], words[2], words[3]);
                                loggedIn = true;
                                System.out.println("Registered! You are now logged in.");
                            } catch (ResponseException e) {
                                switch(e.StatusCode()){
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
                        }
                        break;

                    case "login":
                        if(words.length > 2) {
                            try {
                                serverFacade.login(words[1], words[2]);
                                System.out.println("Login Success.");
                                loggedIn = true;
                            } catch (ResponseException e) {
                                switch(e.StatusCode()){
                                    case 400:
                                        System.out.println("Username does not exist.");
                                        break;
                                    case 401:
                                        System.out.println("Invalid password");
                                        break;
                                    case 500:
                                        System.out.println(e.getMessage());
                                        break;
                                }
                            }
                        } else{
                            System.out.println("Please provide both a username and password");
                        }
                        break;

                    case "quit":
                        System.out.println("See ya!");
                        return;
                }
            }

            // post-login
            while (loggedIn) {
                System.out.println(">>> ");
                String input = Scanner.nextLine();
                String[] words = input.trim().split("\\s+");

                switch (words[0].toLowerCase()) {

                    case "help":
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
                        break;

                    case "create":
                        if (words.length > 1) {
                            try{
                                serverFacade.create(words[1]);
                                System.out.println("Game '" + words[1] + "' created");
                            } catch (ResponseException e) {
                                switch(e.StatusCode()){
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
                        break;

                    case "join":
                        if(words.length > 2) {
                            if(gameList != null) {
                                // if an ID has been provided, observe the game.
                                try{
                                int gameIndex = Integer.parseInt(words[1]) - 1;
                                int gameID = gameList.get(gameIndex).getGameID();
                                serverFacade.join(gameID, words[2]);
                                } catch (ResponseException e) {
                                    switch(e.StatusCode()){
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
                                    }
                                }
                            } else if (gameList.isEmpty()){
                                // if there are no current games.
                                System.out.println("There are no current games. Please create a game to begin");
                            } else {
                                // if the games haven't been listed, provide a list.
                                System.out.println("Here are the current games:");
                                list(gameListObject, gameList);
                                System.out.println("Please type 'join' followed by the game ID number and white/black.");
                            }
                        } else {
                            // no ID provided
                            System.out.println("Here are the current games:");
                            list(gameListObject, gameList);
                            System.out.println("Please type 'join' followed by the game ID number and white/black.");
                        }
                        break;


                    case "list":
                        list(gameListObject, gameList);
                        break;
                    case "observe":
                        if(words.length > 1) {
                            if(gameList != null) {
                                // if an ID has been provided, observe the game.
                                int gameIndex = Integer.parseInt(words[1]) - 1;
                                int gameID = gameList.get(gameIndex).getGameID();
                                serverFacade.observe(gameID);
                            } else if (gameList.isEmpty()){
                                // if there are no current games.
                                System.out.println("There are no current games. Please create a game to begin");
                            } else {
                                // if the games haven't been listed, provide a list.
                                System.out.println("Here are the current games:");
                                list(gameListObject, gameList);
                                System.out.println("Please type 'observe' followed by the game ID number.");
                            }
                        } else {
                            // no ID provided
                            System.out.println("Here are the current games:");
                            list(gameListObject, gameList);
                            System.out.println("Please type 'observe' followed by the game ID number.");
                        }
                        break;

                    case "quit":
                        System.out.println("See ya!");
                        return;

                    case "logout":
                        try{
                            serverFacade.logout();
                            loggedIn = false;
                            System.out.println("Logout Successful");
                            break;
                        } catch (ResponseException e) {
                            System.out.println("Server Error");
                        }
                }

            }
        }
    }

    public void list(ListGamesResponse gameListObject, ArrayList<ListGameShortResponse> gameList){
        try {
            gameListObject = serverFacade.list();
            gameList = gameListObject.getGames();
            if(gameList.size() > 0){
                System.out.println("Here are the current games.");
                int n = 1;
                while (n <= gameList.size()) {
                    ListGameShortResponse game = gameList.get(n - 1);
                    String statement = n + ". " + game.getGameName() + ", ID: " + game.getGameID() + " - White: "
                            + game.getWhiteUsername() + " Black: " + game.getBlackUsername();
                    System.out.println(statement);
                    n++;
                }
            } else {
                System.out.println("There are no current games. Please create a game to begin.");
            }

        } catch(ResponseException e){
            if(e.StatusCode() == 400){
                System.out.println("Server Error");
            } else{
                System.out.println(e.getMessage());
            }
        }
    }

}
