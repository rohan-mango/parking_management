package com.example.parking.entity;
/**
 * Interface defining common behavior for parking spaces
 */
public interface ParkingSpace {
    boolean isOccupied();
    void setOccupied(boolean occupied);
    String getIdentifier();
    VehicleType getAllowedVehicleType();
} 