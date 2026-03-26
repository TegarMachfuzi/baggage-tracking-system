package com.baggage.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/api/auth/**", "/actuator/**", "/fallback/**").permitAll()
                .anyExchange().permitAll()
            .and()
            .httpBasic().disable()
            .formLogin().disable();
        
        return http.build();
    }
}
