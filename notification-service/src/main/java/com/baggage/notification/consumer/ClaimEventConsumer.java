package com.baggage.notification.consumer;

import com.baggage.dto.event.ClaimEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
import com.baggage.notification.service.PassengerClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(ClaimEventConsumer.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @Autowired
    private PassengerClientService passengerClientService;

    @KafkaListener(topics = "claim-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClaimCreated(ClaimEventDto event) {
        log.info("Received claim-created event: claimId={}, baggageId={}, type={}", 
                event.getClaimId(), event.getBaggageId(), event.getClaimType());
        
        try {
            var passenger = passengerClientService.getPassenger(event.getPassengerId());
            String passengerEmail = passenger != null ? passenger.getEmail() : null;

            String subject = templateService.getClaimCreatedEmailSubject();
            String body = templateService.getClaimCreatedEmailBody(
                event.getBaggageId().toString(),
                event.getClaimType()
            );

            if (passengerEmail != null) emailService.sendEmail(passengerEmail, subject, body);

            log.info("Notification sent for claim-created: claimId={}", event.getClaimId());
        } catch (Exception e) {
            log.error("Error processing claim-created event: {}", e.getMessage(), e);
        }
    }
}
