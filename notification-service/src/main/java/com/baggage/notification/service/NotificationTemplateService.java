package com.baggage.notification.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {

    public String getBaggageCreatedEmailSubject() {
        return "Baggage Check-in Confirmation";
    }

    public String getBaggageCreatedEmailBody(String barcode, String flightNumber, String destination) {
        return """
            <html><body style="font-family:Arial,sans-serif;padding:20px">
            <h2 style="color:#2c3e50">Baggage Check-in Confirmation</h2>
            <p>Dear Passenger,</p>
            <p>Your baggage has been successfully checked in.</p>
            <table style="border-collapse:collapse;margin:16px 0">
              <tr><td style="padding:6px 12px;font-weight:bold">Barcode</td><td style="padding:6px 12px">%s</td></tr>
              <tr><td style="padding:6px 12px;font-weight:bold">Flight</td><td style="padding:6px 12px">%s</td></tr>
              <tr><td style="padding:6px 12px;font-weight:bold">Destination</td><td style="padding:6px 12px">%s</td></tr>
            </table>
            {{BARCODE_IMAGE}}
            <p>Please show this barcode at the airport.</p>
            <p style="color:#7f8c8d;font-size:12px">Baggage Tracking Team</p>
            </body></html>
            """.formatted(barcode, flightNumber, destination);
    }

    public String getTrackingUpdatedEmailSubject() {
        return "Baggage Location Update";
    }

    public String getTrackingUpdatedEmailBody(String barcode, String location, String status) {
        return """
            <html><body style="font-family:Arial,sans-serif;padding:20px">
            <h2 style="color:#2c3e50">Baggage Location Update</h2>
            <p>Dear Passenger,</p>
            <p>Your baggage location has been updated.</p>
            <table style="border-collapse:collapse;margin:16px 0">
              <tr><td style="padding:6px 12px;font-weight:bold">Barcode</td><td style="padding:6px 12px">%s</td></tr>
              <tr><td style="padding:6px 12px;font-weight:bold">Location</td><td style="padding:6px 12px">%s</td></tr>
              <tr><td style="padding:6px 12px;font-weight:bold">Status</td><td style="padding:6px 12px">%s</td></tr>
            </table>
            {{BARCODE_IMAGE}}
            <p style="color:#7f8c8d;font-size:12px">Baggage Tracking Team</p>
            </body></html>
            """.formatted(barcode, location, status);
    }

    public String getClaimCreatedEmailSubject() {
        return "Baggage Claim Received";
    }

    public String getClaimCreatedEmailBody(String barcode, String claimType) {
        return """
            <html><body style="font-family:Arial,sans-serif;padding:20px">
            <h2 style="color:#2c3e50">Baggage Claim Received</h2>
            <p>Dear Passenger,</p>
            <p>We have received your baggage claim.</p>
            <table style="border-collapse:collapse;margin:16px 0">
              <tr><td style="padding:6px 12px;font-weight:bold">Barcode</td><td style="padding:6px 12px">%s</td></tr>
              <tr><td style="padding:6px 12px;font-weight:bold">Claim Type</td><td style="padding:6px 12px">%s</td></tr>
            </table>
            <p>Our team will investigate and contact you shortly.</p>
            <p style="color:#7f8c8d;font-size:12px">Baggage Tracking Team</p>
            </body></html>
            """.formatted(barcode, claimType);
    }

    public String getBaggageCreatedSms(String barcode, String flightNumber) {
        return String.format("Baggage checked in. Barcode: %s, Flight: %s", barcode, flightNumber);
    }

    public String getTrackingUpdatedSms(String barcode, String location) {
        return String.format("Baggage update: %s now at %s", barcode, location);
    }
}
