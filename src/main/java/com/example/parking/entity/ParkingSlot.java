package com.example.parking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a physical parking slot in the parking system.
 * Contains information about slot location, occupancy status, and parked vehicle.
 * Implements ParkingSpace interface for standardized space management.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParkingSlot extends BaseEntity implements ParkingSpace {
    /**
     * Reference to the building containing this slot
     */
    private String buildingId;

    /**
     * Reference to the floor containing this slot
     */
    private String floorId;

    /**
     * Current occupancy status of the slot
     */
    private boolean occupied;

    /**
     * Type of vehicle this slot can accommodate
     */
    private VehicleType vehicleType;

    /**
     * Details of the currently parked vehicle, null if unoccupied
     */
    private Vehicle parkedVehicle;

    /**
     * Returns unique identifier for this parking slot
     * @return Slot ID in format: buildingId-floorId-vehicleType-number
     */
    @Override
    public String getIdentifier() {
        return getId();
    }

    /**
     * Returns the type of vehicle this slot is designed for
     * @return Vehicle type (TWO_WHEELER or FOUR_WHEELER)
     */
    @Override
    public VehicleType getAllowedVehicleType() {
        return vehicleType;
    }
}