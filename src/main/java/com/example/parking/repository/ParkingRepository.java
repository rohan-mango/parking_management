package com.example.parking.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.entity.BaseEntity;
import com.example.parking.entity.Building;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.ParkingStructure;
import com.example.parking.entity.VehicleType;

/**
 * Repository interface for parking operations
 */
public interface ParkingRepository {
    /**
     * Generic method to save any entity that extends BaseEntity
     */
    <T extends BaseEntity> T save(T entity);
    
    /**
     * Generic method to find any entity by ID
     */
    <T extends BaseEntity> Optional<T> findById(String id, Class<T> entityClass);
    
    /**
     * Find available parking spaces by vehicle type
     */
    List<ParkingSpace> findAvailableSpaces(VehicleType vehicleType);
    
    /**
     * Get all parking structures (buildings and floors)
     */
    <T extends ParkingStructure> List<T> findAllStructures(Class<T> structureClass);
    
    /**
     * Get all buildings
     */
    Map<String, Building> getAllBuildings();
    
    /**
     * Find parking spaces by building and floor
     */
    List<ParkingSpace> findSpacesByStructure(String buildingId, String floorId);
    
    /**
     * Get floor availability details
     */
    FloorAvailabilityDTO getFloorAvailability(String buildingId, String floorId);
    
    /**
     * Get all parking slots in the system
     * @return List of all parking slots
     */
    List<ParkingSlot> getAllSlots();
    
    /**
     * Find available parking slots by vehicle type
     * @param vehicleType Type of vehicle
     * @return List of available parking slots
     */
    List<ParkingSlot> findAvailableSlots(VehicleType vehicleType);
    
    /**
     * Update parking slot information
     * @param slot The parking slot to update
     */
    void updateSlot(ParkingSlot slot);
    
    /**
     * Find parking slot by ID
     * @param id Slot ID
     * @return Optional of ParkingSlot
     */
    Optional<ParkingSlot> findById(String id);
} 