package com.baggage.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void cache(String key, Object value, long ttl, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
            log.info("Cached data with key: {}", key);
        } catch (Exception e) {
            log.error("Error caching data: {}", e.getMessage());
        }
    }
    
    public void invalidate(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Invalidated cache with key: {}", key);
        } catch (Exception e) {
            log.error("Error invalidating cache: {}", e.getMessage());
        }
    }
    
    public void invalidatePattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Invalidated {} cache entries with pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error invalidating cache pattern: {}", e.getMessage());
        }
    }
}
