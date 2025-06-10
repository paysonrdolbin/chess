package ui;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import exception.responseException;
import model.UserData;
import request.*;
import response.*;

public class ServerFacade {
    private String authToken = null;
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public int create(String gameName) throws responseException {
        CreateJsonBody request = new CreateJsonBody(gameName);
        CreateGameResponse response = makeRequest("POST", "/game", request, CreateGameResponse.class);
        return response.getGameID();
    }
    public void logout() throws responseException {
        makeRequest("DELETE", "/session", null, LogoutResponse.class);
        authToken = null;
    }
    public ListGamesResponse list() throws responseException {
        return makeRequest("GET", "/game", null, ListGamesResponse.class);
    }

    public void register(String username, String password, String email) throws responseException {
        UserData user = new UserData(username, password, email);
        RegisterResponse response = makeRequest("POST", "/user", user, RegisterResponse.class);
        authToken = response.getAuthToken();
    }

    public void login(String username, String password) throws responseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResponse response = makeRequest("POST", "/session", loginRequest, LoginResponse.class);
        authToken = response.getAuthToken();
    }

    public void observe(int gameID){
    }

    public void join(int gameID, String teamColor) throws responseException {
        JoinJsonBody request = new JoinJsonBody(teamColor, gameID);
        JoinGameResponse response = makeRequest("PUT", "/game", request, JoinGameResponse.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws responseException {
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (responseException e) {
            throw e;
        } catch (Exception e){
            throw new responseException(500, e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException{
        if(request != null){
            http.addRequestProperty("Content-type", "application/json");
            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()){
                reqBody.write(reqData.getBytes());
            }

        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, responseException {
        var status = http.getResponseCode();
        if(!isSuccessful(status)) {
            try(InputStream respErr = http.getErrorStream()){
                if(respErr != null){
                    throw responseException.fromJson(respErr);
                }
            }

            throw new responseException(status, "other failure: " + status);
        }
    }

    private boolean isSuccessful(int status){
        return status / 100 == 2;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0){
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if(responseClass != null) {
                    Gson gson = new Gson();
                    response = gson.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

}
