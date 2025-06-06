package ui;

import model.ListGameShortResponse;
import java.util.ArrayList;

public class ServerFacade {
    private final String authToken = null;


    public int create(String gameName){
        return 1234;
    }
    public void logout(){

    }
    public ArrayList<ListGameShortResponse> list(){
        ArrayList<ListGameShortResponse> games = new ArrayList<ListGameShortResponse>();
        return games;
    }

    public void register(String username, String password, String email){}

    public void login(String username, String password){}

    public void observe(int gameID){}

    public void join(int gameID, String teamColor){}
}
