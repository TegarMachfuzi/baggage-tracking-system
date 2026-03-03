package com.baggage.gateway.controller;

import com.baggage.gateway.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // TODO: Validate credentials against user service
        // For now, accept any username/password for demo
        
        String token = jwtUtil.generateToken(request.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("username", request.getUsername());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // TODO: Create user in user service
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", request.getUsername());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            if (isValid) {
                response.put("username", jwtUtil.extractUsername(token));
            }
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.badRequest().body("Invalid Authorization header");
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}

@Data
class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
