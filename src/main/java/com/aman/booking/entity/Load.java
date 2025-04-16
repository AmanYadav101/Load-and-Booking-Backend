package com.aman.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotBlank(message = "Shipper ID is required")
    private String shipperId;

    @Valid
    @Embedded
    @NotNull(message = "Facility details are required")
    private Facility facility;

    @NotBlank(message = "Product type is required")
    private String productType;

    @NotBlank(message = "Truck type is required")
    private String truckType;

    @Min(value = 1, message = "Number of trucks must be at least 1")
    private int noOfTrucks;

    @Positive(message = "Weight must be a positive value")
    private double weight;

    private String comment;

    @NotNull(message = "Date posted is required")
    private Timestamp datePosted;

    @Pattern(regexp = "POSTED|BOOKED|CANCELLED", message = "Status must be POSTED, BOOKED, or CANCELLED")
    private String status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShipperId() {
        return shipperId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public int getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setNoOfTrucks(int noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Timestamp datePosted) {
        this.datePosted = datePosted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
