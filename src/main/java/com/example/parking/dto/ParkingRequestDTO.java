package com.example.parking.dto;

import com.example.parking.entity.VehicleType;

import lombok.Data;

/**
 * Data Transfer Object for parking requests.
 * Encapsulates vehicle information needed for parking allocation.
 */
@Data
public class ParkingRequestDTO {
    /**
     * Vehicle's registration/license plate number
     */
    private String registrationNumber;
    
    /**
     * Type of vehicle requesting parking
     */
    private VehicleType vehicleType;
} 