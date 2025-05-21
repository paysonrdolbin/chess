package service;

import dataAccess.GameDAO;
import dataAccess.UserDAO;
import dataAccess.AuthDAO;
import response.ClearResponse;
import request.ClearRequest;


public class ClearService {
    public ClearResponse clearDB(ClearRequest request){
        GameDAO.clear();
        UserDAO.clear();
        AuthDAO.clear();
        ClearResponse response = new ClearResponse();
        return response;
    }
}
