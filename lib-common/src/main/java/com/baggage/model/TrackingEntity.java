package com.baggage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracking")
public class TrackingEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "baggage_id", nullable = false)
    private UUID baggageId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "remarks")
    private String remarks;

    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public TrackingEntity() {
    }

    public TrackingEntity(UUID id, UUID baggageId, String location, String status, LocalDateTime timestamp, String remarks) {
        this.id = id;
        this.baggageId = baggageId;
        this.location = location;
        this.status = status;
        this.timestamp = timestamp;
        this.remarks = remarks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBaggageId() {
        return baggageId;
    }

    public void setBaggageId(UUID baggageId) {
        this.baggageId = baggageId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
