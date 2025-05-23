package response;

import model.ListGameResponseBody;

import java.util.ArrayList;

public class ListGamesResponse {
    private final ArrayList<ListGameResponseBody> games;

    public ListGamesResponse(ArrayList<ListGameResponseBody> games) {
        this.games = games;
    }
}
