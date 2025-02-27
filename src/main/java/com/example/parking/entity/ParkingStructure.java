package com.example.parking.entity;

/**
 * Interface for parking structures (buildings, floors)
 */
public interface ParkingStructure {
    String getIdentifier();
    int getTotalCapacity();
    int getAvailableCapacity();
} 