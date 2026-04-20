package com.baggage.notification.service;

import com.baggage.dto.response.PassengerResDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class PassengerClientService {

    private static final Logger log = LoggerFactory.getLogger(PassengerClientService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${passenger.service.url:http://passenger-service:8082}")
    private String passengerServiceUrl;

    public PassengerResDto getPassenger(UUID passengerId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = passengerServiceUrl + "/api/passengers/" + passengerId;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            return objectMapper.treeToValue(root.get("data"), PassengerResDto.class);
        } catch (Exception e) {
            log.warn("Failed to fetch passenger {}: {}", passengerId, e.getMessage());
            return null;
        }
    }
}
