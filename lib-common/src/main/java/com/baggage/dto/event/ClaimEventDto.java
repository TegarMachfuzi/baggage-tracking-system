package com.baggage.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClaimEventDto {
    private UUID claimId;
    private UUID baggageId;
    private UUID passengerId;
    private String claimType;
    private String status;
    private String description;
    private LocalDateTime timestamp;

    public ClaimEventDto() {
    }

    public UUID getClaimId() {
        return claimId;
    }

    public void setClaimId(UUID claimId) {
        this.claimId = claimId;
    }

    public UUID getBaggageId() {
        return baggageId;
    }

    public void setBaggageId(UUID baggageId) {
        this.baggageId = baggageId;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(UUID passengerId) {
        this.passengerId = passengerId;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
