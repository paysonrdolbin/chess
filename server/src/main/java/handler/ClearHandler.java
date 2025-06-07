package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.ClearRequest;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {
    private final Gson gson = new Gson();

    // default constructor
    public ClearHandler() {
    }
    @Override
    public Object handle(Request req, Response res) {
        try{
            ClearService service = new ClearService();
            ClearRequest clearRequest = new ClearRequest();
            service.clearDB(clearRequest);
            res.status(200);
            return "{}";
        } catch (DataAccessException e){
            res.status(500);
            return gson.toJson(Map.of("message",e.getMessage()));
        }

    }
}
