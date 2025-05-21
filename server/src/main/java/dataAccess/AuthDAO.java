package dataAccess;

import memoryDAO.MemoryAuthDAO;

public class AuthDAO {
    private final static MemoryAuthDAO authDB = new MemoryAuthDAO();

    public static void clear() {
        authDB.clear();
    }
}
