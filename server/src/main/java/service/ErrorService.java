package service;

import com.google.gson.Gson;
import spark.Response;

import java.util.Map;

public class ErrorService {
    public static int errorCode(String message){
        if(message.equals("Error: unauthorized")){
            return 401;
        } else if(message.equals("Error: bad request")){
            return 400;
        } else if(message.equals("Error: already taken")){
            return 403;
        } else{
            return 500;
        }
    }

    public static String handleException(Exception e, Response res, Gson gson) {
        int statusCode;

        if (e instanceof IllegalArgumentException) {
            statusCode = errorCode(e.getMessage());
        } else {
            statusCode = 500;
        }

        res.status(statusCode);
        return gson.toJson(Map.of("message", e.getMessage(), "status", statusCode));
    }

}
