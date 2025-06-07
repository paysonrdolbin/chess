package request;

public class CreateJsonBody {
    public String getGameName() {
        return gameName;
    }

    private final String gameName;

    public CreateJsonBody(String gameName) {
        this.gameName = gameName;
    }
}
