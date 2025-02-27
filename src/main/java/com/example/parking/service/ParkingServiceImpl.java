package com.example.parking.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.parking.dto.BuildingCapacityDTO;
import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.dto.ParkingRequestDTO;
import com.example.parking.dto.ParkingResponseDTO;
import com.example.parking.entity.Building;
import com.example.parking.entity.Floor;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.VehicleType;
import com.example.parking.repository.ParkingRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of parking management business logic.
 * Handles vehicle parking, slot management, and capacity tracking.
 * Uses in-memory repository for data persistence.
 */
@Service
@Slf4j
public class ParkingServiceImpl implements ParkingService {
    /**
     * Repository for parking data operations
     * Handles CRUD operations for parking entities
     */
    private final ParkingRepository parkingRepository;

    /**
     * Constructor injection for ParkingRepository
     * @param parkingRepository Repository for parking data operations
     */
    public ParkingServiceImpl(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    /**
     * Checks and returns the available capacity for each vehicle type
     * @return Map of vehicle types and their available slot counts
     */
    @Override
    public List<BuildingCapacityDTO> checkCapacity() {
        return parkingRepository.findAllStructures(Building.class).stream()
            .map(this::mapBuildingToCapacityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Maps building entity to DTO with capacity information
     * Calculates available slots for each vehicle type
     * @param building Building entity to be mapped
     * @return DTO containing building capacity information
     */
    private BuildingCapacityDTO mapBuildingToCapacityDTO(Building building) {
        BuildingCapacityDTO dto = new BuildingCapacityDTO();
        dto.setBuildingId(building.getBuildingId());
        dto.setFloors(building.getFloors().stream()
            .map(this::mapFloorToCapacityDTO)
            .collect(Collectors.toList()));
        return dto;
    }

    private BuildingCapacityDTO.FloorCapacityDTO mapFloorToCapacityDTO(Floor floor) {
        BuildingCapacityDTO.FloorCapacityDTO dto = new BuildingCapacityDTO.FloorCapacityDTO();
        dto.setFloorId(floor.getFloorId());
        
        Map<VehicleType, Integer> availableSlots = new EnumMap<>(VehicleType.class);
        for (VehicleType type : VehicleType.values()) {
            availableSlots.put(type, (int) floor.getParkingSlots().stream()
                .filter(slot -> slot.getVehicleType() == type && !slot.isOccupied())
                .count());
        }
        dto.setAvailableSlots(availableSlots);
        return dto;
    }

    /**
     * Checks if a specific parking slot is occupied or available
     * @param slotId ID of the slot to check
     * @return Response containing slot status information
     */
    @Override
    public ParkingResponseDTO checkSlotStatus(String slotId) {
        return parkingRepository.findById(slotId, ParkingSlot.class)
            .map(slot -> {
                ParkingResponseDTO response = new ParkingResponseDTO();
                response.setSlotId(slot.getId());
                response.setSuccess(true);
                response.setMessage(slot.isOccupied() ? "Occupied" : "Available");
                return response;
            })
            .orElseGet(() -> {
                ParkingResponseDTO response = new ParkingResponseDTO();
                response.setSuccess(false);
                response.setMessage("Slot not found");
                return response;
            });
    }

    /**
     * Attempts to park a vehicle in an available slot
     * Implements the core parking logic:
     * 1. Finds available slot
     * 2. Creates vehicle record
     * 3. Updates slot status
     * 4. Persists changes
     * @param request Vehicle parking request with type and registration
     * @return Response with parking status and allocated slot
     */
    @Override
    public ParkingResponseDTO parkVehicle(ParkingRequestDTO request) {
        List<ParkingSpace> availableSpaces = parkingRepository.findAvailableSpaces(request.getVehicleType());
        
        if (availableSpaces.isEmpty()) {
            ParkingResponseDTO response = new ParkingResponseDTO();
            response.setSuccess(false);
            response.setMessage("No available slots for " + request.getVehicleType());
            return response;
        }

        ParkingSlot slot = (ParkingSlot) availableSpaces.get(0);
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setType(request.getVehicleType());
        
        slot.setOccupied(true);
        slot.setParkedVehicle(vehicle);
        parkingRepository.save(slot);

        ParkingResponseDTO response = new ParkingResponseDTO();
        response.setSuccess(true);
        response.setSlotId(slot.getId());
        response.setMessage("Vehicle parked successfully");
        return response;
    }

    @Override
    public FloorAvailabilityDTO getFloorAvailability(String buildingId, String floorId) {
        return parkingRepository.getFloorAvailability(buildingId, floorId);
    }
} 