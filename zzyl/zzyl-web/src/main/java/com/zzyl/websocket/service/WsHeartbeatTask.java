package com.zzyl.websocket.service;

import com.zzyl.websocket.config.WebSocketProperties;
import com.zzyl.websocket.session.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsHeartbeatTask {

    private final WsSessionRegistry sessionRegistry;
    private final WebSocketProperties webSocketProperties;
    private final WsMetricsService metricsService;

    @Scheduled(fixedDelayString = "${zzyl.framework.websocket.heartbeat-interval-ms:15000}")
    public void pingAndClean() {
        for (WebSocketSession session : sessionRegistry.allSessions()) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
            } catch (IOException e) {
                metricsService.onError();
                log.warn("ping websocket session failed, session={}", session.getId(), e);
            }
        }
        sessionRegistry.closeIdleSessions(webSocketProperties.getIdleTimeoutMs());
    }
}
