package com.baggage.notification.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {
    
    public String getBaggageCreatedEmailSubject() {
        return "Baggage Check-in Confirmation";
    }
    
    public String getBaggageCreatedEmailBody(String barcode, String flightNumber, String destination) {
        return String.format("""
            Dear Passenger,
            
            Your baggage has been successfully checked in.
            
            Details:
            - Barcode: %s
            - Flight: %s
            - Destination: %s
            
            You can track your baggage status anytime using the barcode.
            
            Thank you for choosing our service.
            
            Best regards,
            Baggage Tracking Team
            """, barcode, flightNumber, destination);
    }
    
    public String getTrackingUpdatedEmailSubject() {
        return "Baggage Location Update";
    }
    
    public String getTrackingUpdatedEmailBody(String barcode, String location, String status) {
        return String.format("""
            Dear Passenger,
            
            Your baggage location has been updated.
            
            Details:
            - Barcode: %s
            - Current Location: %s
            - Status: %s
            
            Track your baggage anytime using the barcode.
            
            Best regards,
            Baggage Tracking Team
            """, barcode, location, status);
    }
    
    public String getClaimCreatedEmailSubject() {
        return "Baggage Claim Received";
    }
    
    public String getClaimCreatedEmailBody(String barcode, String claimType) {
        return String.format("""
            Dear Passenger,
            
            We have received your baggage claim.
            
            Details:
            - Barcode: %s
            - Claim Type: %s
            
            Our team will investigate and contact you shortly.
            
            Best regards,
            Baggage Tracking Team
            """, barcode, claimType);
    }
    
    public String getBaggageCreatedSms(String barcode, String flightNumber) {
        return String.format("Baggage checked in. Barcode: %s, Flight: %s. Track at baggagetracking.com", 
            barcode, flightNumber);
    }
    
    public String getTrackingUpdatedSms(String barcode, String location) {
        return String.format("Baggage update: %s now at %s", barcode, location);
    }
}
