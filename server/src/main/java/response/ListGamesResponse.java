package response;

import response.ListGameShortResponse;

import java.util.ArrayList;

public class ListGamesResponse {
    private final ArrayList<ListGameShortResponse> games;

    public ListGamesResponse(ArrayList<ListGameShortResponse> games) {
        this.games = games;
    }
}
