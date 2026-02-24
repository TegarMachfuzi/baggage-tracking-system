package com.baggage.dto.request;

import java.util.UUID;

public class TrackingReqDto {
    private UUID baggageId;
    private String location;
    private String status;
    private String remarks;

    public TrackingReqDto() {
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
