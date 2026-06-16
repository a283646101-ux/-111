package com.itheima.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisConnectionTest {

    @Test
    public void writeAndReadBack() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("127.0.0.1", 6379);
        config.setPassword(RedisPassword.of("123456"));
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        RedisConnection connection = factory.getConnection();
        connection.serverCommands().setConfig("stop-writes-on-bgsave-error", "no");
        connection.close();
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        String key = "redis:test:key";
        String value = "redis:test:value";
        template.opsForValue().set(key, value);
        String back = template.opsForValue().get(key);
        Assertions.assertEquals(value, back);
        factory.destroy();
    }
}
