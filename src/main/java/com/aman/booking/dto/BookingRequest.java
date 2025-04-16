package com.aman.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.sql.Timestamp;
import java.util.UUID;

public class BookingRequest {
    @NotNull(message = "Load ID is required")
    private UUID loadId;

    @NotBlank(message = "Transporter ID is required")
    private String transporterId;

    @Positive(message = "Proposed rate must be a positive value")
    private double proposedRate;

    private String comment;

    @Pattern(regexp = "PENDING|ACCEPTED|REJECTED", message = "Status must be PENDING, ACCEPTED, or REJECTED")
    private String status = "PENDING";

    @NotNull(message = "Request timestamp is required")
    private Timestamp requestedAt;
    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }

    public double getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(double proposedRate) {
        this.proposedRate = proposedRate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }
}
