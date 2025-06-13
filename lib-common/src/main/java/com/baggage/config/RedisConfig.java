package com.baggage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPortString;

    @Value("${spring.redis.max.total}")
    private String redisMaxTotalString;

    @Value("${spring.redis.max.idle}")
    private String redisMaxIdleString;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        Integer redisPort = Integer.parseInt(redisPortString);
        Integer redisMaxTotal = Integer.parseInt(redisMaxTotalString);
        Integer redisMaxIdle = Integer.parseInt(redisMaxIdleString);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);

        JedisConnectionFactory connection = new JedisConnectionFactory(config);
        connection.getPoolConfig().setMaxIdle(redisMaxIdle);
        connection.getPoolConfig().setMaxTotal(redisMaxTotal);

        return connection;
    }

    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
