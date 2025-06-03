package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import memorydao.MemoryAuthDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import response.CreateGameResponse;
import response.ListGamesResponse;
import response.LoginResponse;
import response.RegisterResponse;
import service.GameService;
import model.GameData;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private ClearService clearService = new ClearService();
    @Test
    public void clearWorks() throws DataAccessException {
        ClearRequest clearRequest = new ClearRequest();
        clearService.clearDB(clearRequest);
    }

}
