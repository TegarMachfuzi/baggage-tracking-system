package com.baggage.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/actuator/health"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        // Skip authentication for public paths
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        
        // Extract token from Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        // Validate token
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Add username to request header
        String username = jwtUtil.extractUsername(token);
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", username)
                .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
    
    @Override
    public int getOrder() {
        return -100; // Execute before other filters
    }
}
