package com.baggage.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PassengerClient {

    private final RestTemplate restTemplate;

    @Value("${passenger.service.url:http://localhost:8082}")
    private String passengerServiceUrl;

    @Value("${passenger.service.path:/api/passengers}")
    private String passengerServicePath;

    public PassengerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPassenger(String name, String email, String phone,
                                   String passportNumber, String nationality) {
        Map<String, String> body = Map.of(
            "name", name,
            "email", email,
            "phone", phone != null ? phone : "",
            "passportNumber", passportNumber,
            "nationality", nationality
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
            passengerServiceUrl + passengerServicePath, body, Map.class
        );

        if (response != null && response.get("data") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return (String) data.get("id");
        }
        return null;
    }
}
