package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import response.ClearResponse;
import request.ClearRequest;


public class ClearService {
    public ClearResponse clearDB(ClearRequest request) throws DataAccessException {
        GameDAO.clear();
        UserDAO.clear();
        AuthDAO.clear();
        ClearResponse response = new ClearResponse();
        return response;
    }
}
