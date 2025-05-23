package service;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
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
