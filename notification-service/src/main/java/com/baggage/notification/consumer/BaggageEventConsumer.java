package com.baggage.notification.consumer;

import com.baggage.dto.event.BaggageEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
import com.baggage.notification.service.PassengerClientService;
import com.baggage.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BaggageEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageEventConsumer.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @Autowired
    private PassengerClientService passengerClientService;

    @KafkaListener(topics = "baggage-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBaggageCreated(BaggageEventDto event) {
        log.info("Received baggage-created event: baggageId={}, barcode={}", 
                event.getBaggageId(), event.getBarcode());
        
        try {
            var passenger = passengerClientService.getPassenger(event.getPassengerId());
            String passengerEmail = passenger != null ? passenger.getEmail() : null;
            String passengerPhone = passenger != null ? passenger.getPhone() : null;

            String subject = templateService.getBaggageCreatedEmailSubject();
            String body = templateService.getBaggageCreatedEmailBody(
                event.getBarcode(), event.getFlightNumber(), event.getDestination());

            if (passengerEmail != null) emailService.sendEmail(passengerEmail, subject, body);

            String smsMessage = templateService.getBaggageCreatedSms(event.getBarcode(), event.getFlightNumber());
            if (passengerPhone != null) smsService.sendSms(passengerPhone, smsMessage);

            log.info("Notifications sent for baggage-created: {}", event.getBarcode());
        } catch (Exception e) {
            log.error("Error processing baggage-created event: {}", e.getMessage(), e);
        }
    }
}
