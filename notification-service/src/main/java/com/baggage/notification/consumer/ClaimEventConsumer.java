package com.baggage.notification.consumer;

import com.baggage.dto.event.ClaimEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
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
    
    @KafkaListener(topics = "claim-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClaimCreated(ClaimEventDto event) {
        log.info("Received claim-created event: claimId={}, baggageId={}, type={}", 
                event.getClaimId(), event.getBaggageId(), event.getClaimType());
        
        try {
            // TODO: Get passenger email from passenger-service
            String passengerEmail = "passenger@example.com"; // Mock
            String barcode = "MOCK-BARCODE"; // Mock
            
            // Send email notification
            String subject = templateService.getClaimCreatedEmailSubject();
            String body = templateService.getClaimCreatedEmailBody(
                barcode,
                event.getClaimType().toString()
            );
            emailService.sendEmail(passengerEmail, subject, body);
            
            log.info("Notification sent for claim-created: claimId={}", event.getClaimId());
        } catch (Exception e) {
            log.error("Error processing claim-created event: {}", e.getMessage(), e);
        }
    }
}
