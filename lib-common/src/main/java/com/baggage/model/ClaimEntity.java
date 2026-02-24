package com.baggage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim")
public class ClaimEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "baggage_id", nullable = false)
    private UUID baggageId;

    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "claim_type", nullable = false)
    private String claimType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public ClaimEntity() {
    }

    public ClaimEntity(UUID id, UUID baggageId, UUID passengerId, String claimType, String status, String description, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.baggageId = baggageId;
        this.passengerId = passengerId;
        this.claimType = claimType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
