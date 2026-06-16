package com.zzyl.websocket.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WsClientSession {

    private final WebSocketSession session;
    private final String userId;
    private final String clientId;
    private final Set<String> rooms = ConcurrentHashMap.newKeySet();
    @Setter
    private volatile long lastSeenAt = System.currentTimeMillis();

    public WsClientSession(WebSocketSession session, String userId, String clientId) {
        this.session = session;
        this.userId = userId;
        this.clientId = clientId;
    }
}
