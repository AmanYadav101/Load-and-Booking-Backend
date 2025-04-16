package com.aman.booking.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

@Embeddable
public class Facility {

    @NotBlank(message = "Loading point is required")
    private String loadingPoint;

    @NotBlank(message = "Unloading point is required")
    private String unloadingPoint;

    @NotNull(message = "Loading date is required")
    private Timestamp loadingDate;

    @NotNull(message = "Unloading date is required")
    @Future(message = "Unloading date must be in the future")
    private Timestamp unloadingDate;

    public String getLoadingPoint() {
        return loadingPoint;
    }

    public void setLoadingPoint(String loadingPoint) {
        this.loadingPoint = loadingPoint;
    }

    public String getUnloadingPoint() {
        return unloadingPoint;
    }

    public void setUnloadingPoint(String unloadingPoint) {
        this.unloadingPoint = unloadingPoint;
    }

    public Timestamp getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Timestamp loadingDate) {
        this.loadingDate = loadingDate;
    }

    public Timestamp getUnloadingDate() {
        return unloadingDate;
    }

    public void setUnloadingDate(Timestamp unloadingDate) {
        this.unloadingDate = unloadingDate;
    }
}
