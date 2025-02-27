package com.example.parking.dto;

import lombok.Data;

/**
 * Data Transfer Object for parking operation responses.
 * Contains operation result and relevant parking information.
 */
@Data
public class ParkingResponseDTO {
    /**
     * Allocated or referenced parking slot ID
     */
    private String slotId;
    
    /**
     * Descriptive message about the operation result
     */
    private String message;
    
    /**
     * Operation success indicator
     */
    private boolean success;
} 