package com.baggage.dto;

import jakarta.persistence.*;

import java.util.UUID;

public class PassengerDto {

    private UUID id;

    private String name;

    private String email;

    private String phone;

    private String bookingRef;

    private String flightInfo;

    public PassengerDto(String bookingRef, String email, String flightInfo, UUID id, String name, String phone) {
        this.bookingRef = bookingRef;
        this.email = email;
        this.flightInfo = flightInfo;
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public PassengerDto() {

    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(String flightInfo) {
        this.flightInfo = flightInfo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
