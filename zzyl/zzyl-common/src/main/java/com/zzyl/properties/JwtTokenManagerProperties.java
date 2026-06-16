package com.zzyl.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zzyl.framework.jwt")
public class JwtTokenManagerProperties {

    private String base64EncodedSecretKey;

    private Integer ttl;
}
