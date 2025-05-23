package handler;

import request.ClearRequest;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    // default constructor
    public ClearHandler() {
    }
    @Override
    public Object handle(Request req, Response res) {
        ClearService service = new ClearService();
        ClearRequest clearRequest = new ClearRequest();
        service.clearDB(clearRequest);
        res.status(200);
        return "{}";
    }
}
