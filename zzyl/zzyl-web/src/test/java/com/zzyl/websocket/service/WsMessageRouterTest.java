package com.zzyl.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzyl.websocket.protocol.WsMessage;
import com.zzyl.websocket.protocol.WsMessageType;
import com.zzyl.websocket.session.WsClientSession;
import com.zzyl.websocket.session.WsSessionRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WsMessageRouterTest {

    @Test
    public void shouldDirectMessageToTargetUserSessions() throws Exception {
        WsMetricsService metricsService = new WsMetricsService();
        WsSessionRegistry registry = new WsSessionRegistry(metricsService);
        WsMessageRouter router = new WsMessageRouter(registry, metricsService, new ObjectMapper());

        WebSocketSession sender = Mockito.mock(WebSocketSession.class);
        Mockito.when(sender.getId()).thenReturn("s1");
        Mockito.when(sender.isOpen()).thenReturn(true);
        registry.register(sender, "u1", "c1");

        WebSocketSession receiver = Mockito.mock(WebSocketSession.class);
        Mockito.when(receiver.getId()).thenReturn("s2");
        Mockito.when(receiver.isOpen()).thenReturn(true);
        registry.register(receiver, "u2", "c2");

        WsMessage message = new WsMessage();
        message.setType(WsMessageType.DIRECT);
        message.setTo("u2");
        message.setContent("hello");
        int delivered = router.direct("u2", message);

        Assert.assertEquals(1, delivered);
        Mockito.verify(receiver, Mockito.atLeastOnce()).sendMessage(Mockito.any(TextMessage.class));
    }

    @Test
    public void shouldSubscribeRoomFromRouteText() {
        WsMetricsService metricsService = new WsMetricsService();
        WsSessionRegistry registry = new WsSessionRegistry(metricsService);
        WsMessageRouter router = new WsMessageRouter(registry, metricsService, new ObjectMapper());

        WebSocketSession sender = Mockito.mock(WebSocketSession.class);
        Mockito.when(sender.getId()).thenReturn("s1");
        Mockito.when(sender.isOpen()).thenReturn(true);
        registry.register(sender, "u1", "c1");
        WsClientSession session = registry.getBySessionId("s1");

        WsMessage message = new WsMessage();
        message.setType(WsMessageType.SUBSCRIBE);
        message.setRoom("r1");
        router.routeText(session, message);

        Assert.assertEquals(1, registry.sessionsByRoom("r1").size());
    }
}
