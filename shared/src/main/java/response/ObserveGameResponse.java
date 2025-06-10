package response;

import model.GameData;

public class ObserveGameResponse {
    private final GameData data;
    public ObserveGameResponse(GameData data){
        this.data = data;
    }

    public GameData getData() {
        return data;
    }
}
