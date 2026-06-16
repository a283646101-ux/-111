package com.zzyl.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zzyl.framework.websocket")
public class WebSocketProperties {

    private int sendTimeLimitMs = 10000;

    private int sendBufferSizeLimit = 1024 * 1024;

    private int messageSizeLimit = 64 * 1024;

    private long heartbeatIntervalMs = 15000L;

    private long idleTimeoutMs = 60000L;
}
