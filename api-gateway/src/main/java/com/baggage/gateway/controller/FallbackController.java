package com.baggage.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/baggage")
    public ResponseEntity<?> baggageFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Baggage service is currently unavailable");
        response.put("message", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/passenger")
    public ResponseEntity<?> passengerFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Passenger service is currently unavailable");
        response.put("message", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/tracking")
    public ResponseEntity<?> trackingFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Tracking service is currently unavailable");
        response.put("message", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/claim")
    public ResponseEntity<?> claimFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Claim service is currently unavailable");
        response.put("message", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
