package com.zzyl.websocket.config;

import com.zzyl.websocket.WsConstants;
import com.zzyl.websocket.auth.WebSocketAuthHandshakeInterceptor;
import com.zzyl.websocket.handler.RealtimeWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final RealtimeWebSocketHandler realtimeWebSocketHandler;
    private final WebSocketAuthHandshakeInterceptor authHandshakeInterceptor;
    private final WebSocketProperties webSocketProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(realtimeWebSocketHandler, WsConstants.SERVER_PATH)
                .setAllowedOrigins("*")
                .addInterceptors(authHandshakeInterceptor);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(webSocketProperties.getMessageSizeLimit());
        container.setMaxBinaryMessageBufferSize(webSocketProperties.getMessageSizeLimit());
        container.setMaxSessionIdleTimeout(webSocketProperties.getIdleTimeoutMs());
        return container;
    }
}
