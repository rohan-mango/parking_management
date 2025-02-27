package com.example.parking.dto;

import java.util.List;
import java.util.Map;

import com.example.parking.entity.VehicleType;

import lombok.Data;

/**
 * Data Transfer Object for building capacity information.
 * Provides hierarchical view of parking availability.
 */
@Data
public class BuildingCapacityDTO {

    
    /** Unique identifier for the building */
    private String buildingId;
    
    /** List containing parking capacity information for each floor in the building */
    private List<FloorCapacityDTO> floors;

    /**
     * Inner class representing floor-level capacity details
     */
    @Data
    public static class FloorCapacityDTO {
        /** Unique identifier for the floor */
        private String floorId;
        
        /** Map containing number of available slots for each vehicle type */
        private Map<VehicleType, Integer> availableSlots;
    }
}