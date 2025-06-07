package com.baggage.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "passenger")
public class PassengerEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "passenger_name", nullable = false)
    private String name;

    @Column(name = "passenger_email", nullable = false)
    private String email;

    @Column(name = "passanger_phone", nullable = false)
    private String phone;

    @Column(name = "booking_ref", nullable = false)
    private String bookingRef;

    @Column(name = "flight_info", nullable = false)
    private String flightInfo;

    public PassengerEntity(String bookingRef, String email, String flightInfo, UUID id, String name, String phone) {
        this.bookingRef = bookingRef;
        this.email = email;
        this.flightInfo = flightInfo;
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public PassengerEntity() {

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
