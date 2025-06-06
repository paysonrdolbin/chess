package ui;

import model.ListGameShortResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {
    private final Scanner Scanner = new Scanner(System.in);
    private final ServerFacade serverFacade = new ServerFacade();

    public void run(){
        System.out.println("♕ Welcome to Payson's Chess Server! ♕\nType 'help' to get started.");
        boolean loggedIn = false;
        boolean clientRunning = true;
        ArrayList<ListGameShortResponse> gameList = new ArrayList<>();
        String preloginMessage = """
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
                        System.out.println(preloginMessage);
                        break;
                    case "register":
                        serverFacade.register(words[1], words[2], words[3]);
                        loggedIn = true;
                        break;
                    case "login":
                        serverFacade.login(words[1], words[2]);
                        loggedIn = true;
                        break;
                    case "quit":
                        System.out.println("See ya!");
                        clientRunning = false;
                        break;
                }
            }

            // post-login
            while (clientRunning) {
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
                            serverFacade.create(words[1]);
                        }
                        break;
                    case "join":
                        if(words.length > 1) {
                            serverFacade.join(int)
                        }
                    case "list":
                        gameList = serverFacade.list();
                        int n = 1;
                        while (n <= gameList.size()) {
                            ListGameShortResponse game = gameList.get(n - 1);
                            String statement = n + ". " + game.getGameName() + "ID: " + game.getGameID() + " - White: "
                                    + game.getWhiteUsername() + " Black: " + game.getBlackUsername();
                            System.out.println(statement);
                            n++;
                        }
                        break;
                    case "observe":
                        int gameIndex = Integer.parseInt(words[1]) - 1;
                        int gameID = gameList.get(gameIndex).getGameID();
                        serverFacade.observe(gameID);
                        break;
                    case "quit":
                        System.out.println("See ya!");
                        clientRunning = false;
                        break;
                    case "logout":
                        serverFacade.logout();
                        break;
                }

            }
        }
    }
}
