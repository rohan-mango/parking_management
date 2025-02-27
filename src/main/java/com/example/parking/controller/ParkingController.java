package com.example.parking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.parking.dto.BuildingCapacityDTO;
import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.dto.FloorAvailabilityRequestDTO;
import com.example.parking.dto.ParkingRequestDTO;
import com.example.parking.dto.ParkingResponseDTO;
import com.example.parking.service.ParkingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller handling parking management endpoints.
 * Provides APIs for checking capacity, slot status, and vehicle parking operations.
 * Follows REST principles with proper response handling.
 */
@RestController
@RequestMapping("/api/parking")
@Tag(name = "Parking Management", description = "APIs for managing parking slots")
public class ParkingController {
    /**
     * Service layer dependency for parking operations
     */
    private final ParkingService parkingService;

    /**
     * Constructor injection for ParkingService
     * @param parkingService Service for handling parking operations
     */
    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    /**
     * Retrieves current parking capacity across all buildings
     * @return List of buildings with their floor-wise parking availability
     * @throws ResourceNotFoundException if no buildings are found
     */
    @GetMapping("/capacity")
    @Operation(summary = "Check parking capacity", 
              description = "Returns available slots for each building and floor")
    public ResponseEntity<List<BuildingCapacityDTO>> checkCapacity() {
        return ResponseEntity.ok(parkingService.checkCapacity());
    }

    /**
     * Checks if a specific parking slot is occupied
     * @param slotId Unique identifier of the parking slot (format: B1-F1-TW-01)
     * @return Status of the parking slot including occupancy information
     * @throws SlotNotFoundException if the slot doesn't exist
     */
    @GetMapping("/slot/{slotId}")
    @Operation(summary = "Check slot status", description = "Check if a specific slot is occupied or available")
    public ResponseEntity<ParkingResponseDTO> checkSlotStatus(@PathVariable String slotId) {
        return ResponseEntity.ok(parkingService.checkSlotStatus(slotId));
    }

    /**
     * Parks a vehicle in an available slot
     * @param request Contains vehicle type and registration number
     * @return Details of allocated parking slot or error if no slots available
     * @throws NoAvailableSlotException if no suitable slot is found
     */
    @PostMapping("/park")
    @Operation(summary = "Park a vehicle", description = "Park a vehicle in an available slot")
    public ResponseEntity<ParkingResponseDTO> parkVehicle(@RequestBody ParkingRequestDTO request) {
        return ResponseEntity.ok(parkingService.parkVehicle(request));
    }

    /**
     * Endpoint to get availability details for a specific building floor
     * @param request Contains buildingId and floorId
     * @return ResponseEntity containing floor availability details
     */
    @PostMapping("/availability")
    @Operation(summary = "Get floor availability", 
              description = "Get available parking slots for a specific building floor")
    public ResponseEntity<FloorAvailabilityDTO> getFloorAvailability(
            @RequestBody FloorAvailabilityRequestDTO request) {
        FloorAvailabilityDTO availability = parkingService.getFloorAvailability(
            request.getBuildingId(), 
            request.getFloorId()
        );
        if (availability == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availability);
    }
} 