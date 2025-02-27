package com.example.parking.dto;
import lombok.Data;


/**
 * DTO for requesting floor availability information
 * Encapsulates building and floor identification parameters
 */
@Data
public class FloorAvailabilityRequestDTO {
    /**
     * Identifier for the building
     */
    private String buildingId;
    
    /**
     * Identifier for the floor within the building
     */
    private String floorId;
} 