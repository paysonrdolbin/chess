package dataAccess;
import memoryDAO.MemoryGameDAO;

public class GameDAO {
    private final static MemoryGameDAO gameDB = new MemoryGameDAO();

    public static void clear(){
        gameDB.clear();
    }
}
