package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;
import request.*;

public class ClearServiceTest {
    private ClearService clearService = new ClearService();
    @Test
    public void clearWorks() throws DataAccessException {
        ClearRequest clearRequest = new ClearRequest();
        clearService.clearDB(clearRequest);
    }

}
