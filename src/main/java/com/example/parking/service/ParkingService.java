package com.example.parking.service;

import java.util.List;

import com.example.parking.dto.BuildingCapacityDTO;
import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.dto.ParkingRequestDTO;
import com.example.parking.dto.ParkingResponseDTO;

/**
 * Service interface defining the business operations for parking management.
 * This interface follows the Interface Segregation Principle of SOLID.
 */
public interface ParkingService {
    
    /**
     * Checks the status of a specific parking slot
     * @param slotId ID of the slot to check
     * @return Response containing the slot status
     */
    ParkingResponseDTO checkSlotStatus(String slotId);
    
    /**
     * Parks a vehicle in an available slot
     * @param request Contains vehicle details for parking
     * @return Response containing the parking operation result
     */
    ParkingResponseDTO parkVehicle(ParkingRequestDTO request);

    /**
     * Gets availability details for a specific building floor
     * @param buildingId ID of the building
     * @param floorId ID of the floor
     * @return Floor availability details
     */
    FloorAvailabilityDTO getFloorAvailability(String buildingId, String floorId);

    /**
     * Gets parking capacity details by building and floor
     * @return List of building capacity details
     */
    List<BuildingCapacityDTO> checkCapacity();
} 