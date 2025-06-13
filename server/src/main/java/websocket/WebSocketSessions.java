package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class WebSocketSessions {
    Map<Integer, HashSet<Session>> sessionMap;

    public WebSocketSessions() {
        this.sessionMap = new HashMap<>();
    }

    public void addSessionToGame(int gameID, Session session){
        if (!sessionMap.containsKey(gameID)) {
            sessionMap.put(gameID, new HashSet<>());
        }
        sessionMap.get(gameID).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session){
        sessionMap.get(gameID).remove(session);
    }

    public Set<Session> getSessionsForGame(int gameID){
        return sessionMap.get(gameID);
    }
}
