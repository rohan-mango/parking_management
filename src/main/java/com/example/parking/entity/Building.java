package com.example.parking.entity;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a parking building in the system.
 * Contains multiple floors and manages overall building capacity.
 * Implements ParkingStructure for hierarchical space management.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Building extends BaseEntity implements ParkingStructure {
    /**
     * Unique identifier for the building
     */
    private String buildingId;

    /**
     * List of floors in this building
     */
    private List<Floor> floors = new ArrayList<>();

    /**
     * Gets building's unique identifier
     * @return Building ID (e.g., "B1", "B2")
     */
    @Override
    public String getIdentifier() {
        return buildingId;
    }

    /**
     * Calculates total parking capacity across all floors
     * @return Total number of parking slots in the building
     */
    @Override
    public int getTotalCapacity() {
        return floors.stream()
                .mapToInt(Floor::getTotalCapacity)
                .sum();
    }

    /**
     * Calculates currently available parking spaces
     * @return Number of unoccupied parking slots
     */
    @Override
    public int getAvailableCapacity() {
        return floors.stream()
                .mapToInt(Floor::getAvailableCapacity)
                .sum();
    }
} 