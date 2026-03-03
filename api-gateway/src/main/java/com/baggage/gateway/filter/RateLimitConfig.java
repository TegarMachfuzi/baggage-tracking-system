package com.baggage.gateway.filter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {
    
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Rate limit by user (from JWT)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
    
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Rate limit by IP address
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
}
