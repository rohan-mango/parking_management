package com.example.parking.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a floor within a parking building.
 * Manages collection of parking slots and tracks floor-level capacity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Floor extends BaseEntity implements ParkingStructure {
    /**
     * Floor identifier within the building
     */
    private String floorId;

    /**
     * Reference to parent building
     */
    private String buildingId;

    /**
     * Collection of parking slots on this floor
     */
    private List<ParkingSlot> parkingSlots = new ArrayList<>();

    /**
     * Gets floor's unique identifier
     * @return Floor ID (e.g., "F1", "F2")
     */
    @Override
    public String getIdentifier() {
        return floorId;
    }

    /**
     * Calculates total number of parking slots on this floor
     * @return Total slot count
     */
    @Override
    public int getTotalCapacity() {
        return parkingSlots.size();
    }

    /**
     * Counts number of available parking slots
     * @return Number of unoccupied slots
     */
    @Override
    public int getAvailableCapacity() {
        return (int) parkingSlots.stream()
                .filter(slot -> !slot.isOccupied())
                .count();
    }
} 