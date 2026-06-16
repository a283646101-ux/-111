package com.zzyl.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzyl.websocket.WsConstants;
import com.zzyl.websocket.protocol.WsBinaryEnvelope;
import com.zzyl.websocket.protocol.WsMessage;
import com.zzyl.websocket.service.WsMessageRouter;
import com.zzyl.websocket.service.WsMetricsService;
import com.zzyl.websocket.session.WsClientSession;
import com.zzyl.websocket.session.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeWebSocketHandler extends TextWebSocketHandler {

    private final WsSessionRegistry sessionRegistry;
    private final WsMetricsService metricsService;
    private final WsMessageRouter messageRouter;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        String userId = String.valueOf(attributes.getOrDefault(WsConstants.ATTR_USER_ID, "anonymous"));
        String clientId = String.valueOf(attributes.getOrDefault(WsConstants.ATTR_CLIENT_ID, session.getId()));
        WsClientSession clientSession = sessionRegistry.register(session, userId, clientId);
        Object roomAttributes = attributes.get(WsConstants.ATTR_ROOMS);
        if (roomAttributes instanceof List) {
            for (Object room : (List<?>) roomAttributes) {
                if (room != null && !String.valueOf(room).trim().isEmpty()) {
                    sessionRegistry.joinRoom(session.getId(), String.valueOf(room).trim());
                }
            }
        }
        messageRouter.routeText(clientSession, WsMessage.ack("connected"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        metricsService.onInText();
        WsClientSession clientSession = sessionRegistry.getBySessionId(session.getId());
        if (clientSession == null) {
            session.close(CloseStatus.SERVER_ERROR);
            return;
        }
        WsMessage wsMessage = objectMapper.readValue(message.getPayload(), WsMessage.class);
        messageRouter.routeText(clientSession, wsMessage);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        metricsService.onInBinary();
        WsClientSession clientSession = sessionRegistry.getBySessionId(session.getId());
        if (clientSession == null) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Exception e) {
                metricsService.onError();
            }
            return;
        }
        try {
            WsBinaryEnvelope envelope = WsBinaryEnvelope.from(message.getPayload(), objectMapper);
            messageRouter.routeBinary(clientSession, envelope);
        } catch (Exception e) {
            metricsService.onError();
            messageRouter.routeText(clientSession, WsMessage.error("invalid binary frame"));
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        WsClientSession clientSession = sessionRegistry.getBySessionId(session.getId());
        if (clientSession != null) {
            clientSession.setLastSeenAt(System.currentTimeMillis());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        metricsService.onError();
        log.warn("websocket transport error, session={}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.unregister(session.getId());
    }
}
