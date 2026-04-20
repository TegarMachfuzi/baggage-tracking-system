package com.baggage.notification.consumer;

import com.baggage.dto.event.TrackingEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
import com.baggage.notification.service.PassengerClientService;
import com.baggage.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class TrackingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrackingEventConsumer.class);

    @Autowired private EmailService emailService;
    @Autowired private SmsService smsService;
    @Autowired private NotificationTemplateService templateService;
    @Autowired private PassengerClientService passengerClientService;
    @Autowired private ObjectMapper objectMapper;

    @Value("${baggage.service.url:http://baggage-service:8081}")
    private String baggageServiceUrl;

    @KafkaListener(topics = "tracking-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTrackingUpdated(TrackingEventDto event) {
        log.info("Received tracking-updated event: baggageId={}, location={}, status={}",
                event.getBaggageId(), event.getLocation(), event.getStatus());

        try {
            // Get barcode and passengerId from baggage-service
            String barcode = null;
            UUID passengerId = null;
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = baggageServiceUrl + "/api/baggage/" + event.getBaggageId();
                String response = restTemplate.getForObject(url, String.class);
                JsonNode data = objectMapper.readTree(response).get("data");
                barcode = data.get("barcode").asText();
                passengerId = UUID.fromString(data.get("passengerId").asText());
            } catch (Exception e) {
                log.warn("Failed to fetch baggage info: {}", e.getMessage());
            }

            var passenger = passengerId != null ? passengerClientService.getPassenger(passengerId) : null;
            String passengerEmail = passenger != null ? passenger.getEmail() : null;
            String passengerPhone = passenger != null ? passenger.getPhone() : null;

            String subject = templateService.getTrackingUpdatedEmailSubject();
            String body = templateService.getTrackingUpdatedEmailBody(
                barcode != null ? barcode : event.getBaggageId().toString(),
                event.getLocation(),
                event.getStatus()
            );

            if (passengerEmail != null) emailService.sendEmail(passengerEmail, subject, body);

            String smsMessage = templateService.getTrackingUpdatedSms(
                barcode != null ? barcode : event.getBaggageId().toString(),
                event.getLocation()
            );
            if (passengerPhone != null) smsService.sendSms(passengerPhone, smsMessage);

            log.info("Notifications sent for tracking-updated: baggageId={}", event.getBaggageId());
        } catch (Exception e) {
            log.error("Error processing tracking-updated event: {}", e.getMessage(), e);
        }
    }
}
