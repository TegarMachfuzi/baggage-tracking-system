package com.baggage.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class TrackingResDto {
    private UUID id;
    private UUID baggageId;
    private String location;
    private String status;
    private LocalDateTime timestamp;
    private String remarks;

    public TrackingResDto() {
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
