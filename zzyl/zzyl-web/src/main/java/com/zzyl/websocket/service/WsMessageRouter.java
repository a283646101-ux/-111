package com.zzyl.websocket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzyl.websocket.protocol.WsBinaryEnvelope;
import com.zzyl.websocket.protocol.WsMessage;
import com.zzyl.websocket.protocol.WsMessageType;
import com.zzyl.websocket.session.WsClientSession;
import com.zzyl.websocket.session.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WsMessageRouter {

    private final WsSessionRegistry sessionRegistry;
    private final WsMetricsService metricsService;
    private final ObjectMapper objectMapper;

    public void routeText(WsClientSession sender, WsMessage message) {
        if (message.getType() == null) {
            sendText(sender.getSession(), WsMessage.error("type is required"));
            return;
        }
        sender.setLastSeenAt(System.currentTimeMillis());
        if (message.getTimestamp() != null) {
            metricsService.recordLatency(System.currentTimeMillis() - message.getTimestamp());
        }
        message.setFrom(sender.getUserId());
        switch (message.getType()) {
            case SUBSCRIBE:
                handleSubscribe(sender, message);
                break;
            case UNSUBSCRIBE:
                handleUnsubscribe(sender, message);
                break;
            case BROADCAST:
                broadcast(message);
                break;
            case ROOM:
                broadcastRoom(message.getRoom(), message);
                break;
            case DIRECT:
                direct(message.getTo(), message);
                break;
            case HEARTBEAT:
                sendText(sender.getSession(), WsMessage.ack("heartbeat"));
                break;
            case AUTH:
                sendText(sender.getSession(), WsMessage.ack("authenticated"));
                break;
            default:
                sendText(sender.getSession(), WsMessage.error("unsupported text message type"));
                break;
        }
    }

    public void routeBinary(WsClientSession sender, WsBinaryEnvelope envelope) {
        sender.setLastSeenAt(System.currentTimeMillis());
        if (envelope.getTimestamp() > 0) {
            metricsService.recordLatency(System.currentTimeMillis() - envelope.getTimestamp());
        }
        envelope.setFrom(sender.getUserId());
        WsMessageType type = envelope.getType();
        if (type == null) {
            sendText(sender.getSession(), WsMessage.error("binary type is required"));
            return;
        }
        switch (type) {
            case BROADCAST:
                broadcastBinary(envelope);
                break;
            case ROOM:
                broadcastRoomBinary(envelope.getRoom(), envelope);
                break;
            case DIRECT:
                directBinary(envelope.getTo(), envelope);
                break;
            case HEARTBEAT:
                sendText(sender.getSession(), WsMessage.ack("binary heartbeat"));
                break;
            default:
                sendText(sender.getSession(), WsMessage.error("unsupported binary message type"));
                break;
        }
    }

    public int direct(String userId, WsMessage message) {
        if (!StringUtils.hasText(userId)) {
            return 0;
        }
        List<WebSocketSession> sessions = sessionRegistry.sessionsByUser(userId);
        for (WebSocketSession session : sessions) {
            sendText(session, message);
        }
        return sessions.size();
    }

    public int broadcast(WsMessage message) {
        int count = 0;
        for (WebSocketSession session : sessionRegistry.allSessions()) {
            sendText(session, message);
            count++;
        }
        return count;
    }

    public int broadcastRoom(String room, WsMessage message) {
        if (!StringUtils.hasText(room)) {
            return 0;
        }
        int count = 0;
        for (WebSocketSession session : sessionRegistry.sessionsByRoom(room)) {
            sendText(session, message);
            count++;
        }
        return count;
    }

    public int directBinary(String userId, WsBinaryEnvelope envelope) {
        if (!StringUtils.hasText(userId)) {
            return 0;
        }
        List<WebSocketSession> sessions = sessionRegistry.sessionsByUser(userId);
        for (WebSocketSession session : sessions) {
            sendBinary(session, envelope);
        }
        return sessions.size();
    }

    public int broadcastBinary(WsBinaryEnvelope envelope) {
        int count = 0;
        for (WebSocketSession session : sessionRegistry.allSessions()) {
            sendBinary(session, envelope);
            count++;
        }
        return count;
    }

    public int broadcastRoomBinary(String room, WsBinaryEnvelope envelope) {
        if (!StringUtils.hasText(room)) {
            return 0;
        }
        int count = 0;
        for (WebSocketSession session : sessionRegistry.sessionsByRoom(room)) {
            sendBinary(session, envelope);
            count++;
        }
        return count;
    }

    private void handleSubscribe(WsClientSession sender, WsMessage message) {
        if (!StringUtils.hasText(message.getRoom())) {
            sendText(sender.getSession(), WsMessage.error("room is required"));
            return;
        }
        sessionRegistry.joinRoom(sender.getSession().getId(), message.getRoom());
        sendText(sender.getSession(), WsMessage.ack("subscribed:" + message.getRoom()));
    }

    private void handleUnsubscribe(WsClientSession sender, WsMessage message) {
        if (!StringUtils.hasText(message.getRoom())) {
            sendText(sender.getSession(), WsMessage.error("room is required"));
            return;
        }
        sessionRegistry.leaveRoom(sender.getSession().getId(), message.getRoom());
        sendText(sender.getSession(), WsMessage.ack("unsubscribed:" + message.getRoom()));
    }

    private void sendText(WebSocketSession session, WsMessage message) {
        if (session == null || !session.isOpen()) {
            return;
        }
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                metricsService.onOutText();
            } catch (JsonProcessingException e) {
                metricsService.onError();
                log.warn("serialize text message failed", e);
            } catch (IOException e) {
                metricsService.onError();
                log.warn("send text message failed, session={}", session.getId(), e);
            }
        }
    }

    private void sendBinary(WebSocketSession session, WsBinaryEnvelope envelope) {
        if (session == null || !session.isOpen()) {
            return;
        }
        synchronized (session) {
            try {
                ByteBuffer buffer = WsBinaryEnvelope.toBuffer(envelope, objectMapper);
                session.sendMessage(new BinaryMessage(buffer));
                metricsService.onOutBinary();
            } catch (IOException e) {
                metricsService.onError();
                log.warn("send binary message failed, session={}", session.getId(), e);
            }
        }
    }
}
