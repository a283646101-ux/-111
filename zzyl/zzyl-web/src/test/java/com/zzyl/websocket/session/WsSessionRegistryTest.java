package com.zzyl.websocket.session;

import com.zzyl.websocket.service.WsMetricsService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

public class WsSessionRegistryTest {

    @Test
    public void shouldRegisterJoinRoomAndUnregister() {
        WsMetricsService metricsService = new WsMetricsService();
        WsSessionRegistry registry = new WsSessionRegistry(metricsService);

        WebSocketSession s1 = Mockito.mock(WebSocketSession.class);
        Mockito.when(s1.getId()).thenReturn("s1");
        registry.register(s1, "u1", "c1");
        registry.joinRoom("s1", "r1");

        Assert.assertEquals(1, registry.sessionsByUser("u1").size());
        Assert.assertEquals(1, registry.sessionsByRoom("r1").size());

        registry.unregister("s1");
        Assert.assertEquals(0, registry.sessionsByUser("u1").size());
        Assert.assertEquals(0, registry.sessionsByRoom("r1").size());
    }
}
