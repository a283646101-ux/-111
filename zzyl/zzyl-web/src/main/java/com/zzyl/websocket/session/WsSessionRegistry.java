package com.zzyl.websocket.session;

import com.zzyl.websocket.service.WsMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WsSessionRegistry {

    private final WsMetricsService metricsService;

    private final Map<String, WsClientSession> sessionsById = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userSessionIds = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> roomSessionIds = new ConcurrentHashMap<>();

    public WsClientSession register(WebSocketSession session, String userId, String clientId) {
        WsClientSession clientSession = new WsClientSession(session, userId, clientId);
        sessionsById.put(session.getId(), clientSession);
        userSessionIds.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(session.getId());
        metricsService.onConnect();
        return clientSession;
    }

    public void unregister(String sessionId) {
        WsClientSession removed = sessionsById.remove(sessionId);
        if (removed == null) {
            return;
        }
        Set<String> ids = userSessionIds.getOrDefault(removed.getUserId(), Collections.emptySet());
        ids.remove(sessionId);
        if (ids.isEmpty()) {
            userSessionIds.remove(removed.getUserId());
        }
        for (String room : removed.getRooms()) {
            leaveRoom(sessionId, room);
        }
        metricsService.onDisconnect();
    }

    public WsClientSession getBySessionId(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public void joinRoom(String sessionId, String room) {
        WsClientSession session = sessionsById.get(sessionId);
        if (session == null) {
            return;
        }
        session.getRooms().add(room);
        roomSessionIds.computeIfAbsent(room, ignored -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void leaveRoom(String sessionId, String room) {
        WsClientSession session = sessionsById.get(sessionId);
        if (session != null) {
            session.getRooms().remove(room);
        }
        Set<String> ids = roomSessionIds.get(room);
        if (ids == null) {
            return;
        }
        ids.remove(sessionId);
        if (ids.isEmpty()) {
            roomSessionIds.remove(room);
        }
    }

    public List<WebSocketSession> allSessions() {
        List<WebSocketSession> list = new ArrayList<>(sessionsById.size());
        for (WsClientSession value : sessionsById.values()) {
            list.add(value.getSession());
        }
        return list;
    }

    public List<WebSocketSession> sessionsByUser(String userId) {
        Set<String> ids = userSessionIds.getOrDefault(userId, Collections.emptySet());
        List<WebSocketSession> list = new ArrayList<>(ids.size());
        for (String id : ids) {
            WsClientSession session = sessionsById.get(id);
            if (session != null) {
                list.add(session.getSession());
            }
        }
        return list;
    }

    public List<WebSocketSession> sessionsByRoom(String room) {
        Set<String> ids = roomSessionIds.getOrDefault(room, Collections.emptySet());
        List<WebSocketSession> list = new ArrayList<>(ids.size());
        for (String id : ids) {
            WsClientSession session = sessionsById.get(id);
            if (session != null) {
                list.add(session.getSession());
            }
        }
        return list;
    }

    public void closeIdleSessions(long idleTimeoutMs) {
        long now = System.currentTimeMillis();
        for (WsClientSession clientSession : sessionsById.values()) {
            if (now - clientSession.getLastSeenAt() <= idleTimeoutMs) {
                continue;
            }
            try {
                clientSession.getSession().close(CloseStatus.SESSION_NOT_RELIABLE);
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sessionCount", sessionsById.size());
        map.put("userCount", userSessionIds.size());
        map.put("roomCount", roomSessionIds.size());
        return map;
    }
}
