package com.baggage.user.service;

import com.baggage.user.dto.*;
import com.baggage.user.model.Role;
import com.baggage.user.model.User;
import com.baggage.user.repository.UserRepository;
import com.baggage.user.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);
        user.setEnabled(true);
        
        user = repository.save(user);
        
        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find user
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        // Check if enabled
        if (!user.getEnabled()) {
            throw new RuntimeException("Account is disabled");
        }
        
        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
    
    public UserResponse getUserByUsername(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return mapToResponse(user);
    }
    
    public UserResponse getUserById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return mapToResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }
    
    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setEnabled(user.getEnabled());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
