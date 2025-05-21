package handler;

import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler {
    @Override
    public Object handle(Request req, Response res) throws Exception{
        ClearService service = new ClearService();
        service.clear();
        res.status(200);
        return "{}";
    }
}
