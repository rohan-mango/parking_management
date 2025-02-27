package com.example.parking.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.parking.dto.BuildingCapacityDTO;
import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.dto.ParkingRequestDTO;
import com.example.parking.dto.ParkingResponseDTO;
import com.example.parking.entity.Building;
import com.example.parking.entity.Floor;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.VehicleType;
import com.example.parking.repository.ParkingRepository;
/**
 * Test class for ParkingServiceImpl
 * Tests business logic implementation
 */
@ExtendWith(MockitoExtension.class)
class ParkingServiceImplTest {
    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingServiceImpl parkingService;

    /**
     * Test for checking parking capacity
     */
    @Test
    @DisplayName("Should return capacity for all buildings")
    void checkCapacity_ShouldReturnAllBuildingsCapacity() {
        // Arrange
        Map<String, Building> testBuildings = new HashMap<>();
        Building building = new Building();
        building.setBuildingId("B1");
        building.setFloors(new ArrayList<>()); // Initialize floors list
        testBuildings.put("B1", building);
        
        when(parkingRepository.getAllBuildings()).thenReturn(testBuildings);

        // Act
        List<BuildingCapacityDTO> result = parkingService.checkCapacity();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Test for checking slot status
     */
    @Test
    @DisplayName("Should check slot status correctly")
    void checkSlotStatus_WhenSlotExists_ShouldReturnStatus() {
        // Arrange
        String slotId = "B1-F1-TW-01";
        ParkingSlot slot = new ParkingSlot();
        slot.setId(slotId);
        slot.setOccupied(false);
        
        when(parkingRepository.findById(slotId, ParkingSlot.class))
            .thenReturn(Optional.of(slot));

        // Act
        ParkingResponseDTO response = parkingService.checkSlotStatus(slotId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Available", response.getMessage());
    }

    /**
     * Test for parking vehicle
     */
    @Test
    @DisplayName("Should park vehicle successfully")
    void parkVehicle_WhenSlotAvailable_ShouldParkSuccessfully() {
        // Arrange
        ParkingRequestDTO request = new ParkingRequestDTO();
        request.setVehicleType(VehicleType.TWO_WHEELER);
        request.setRegistrationNumber("KA01AB1234");

        ParkingSlot availableSlot = new ParkingSlot();
        availableSlot.setId("B1-F1-TW-01");
        availableSlot.setOccupied(false);
        
        when(parkingRepository.findAvailableSlots(VehicleType.TWO_WHEELER))
            .thenReturn(Collections.singletonList(availableSlot));
        when(parkingRepository.save(any(ParkingSlot.class))).thenReturn(availableSlot);

        // Act
        ParkingResponseDTO response = parkingService.parkVehicle(request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Vehicle parked successfully", response.getMessage());
    }

    /**
     * Test for floor availability
     */
    @Test
    void getFloorAvailability_ShouldReturnAvailability() {
        // Arrange
        String buildingId = "B1";
        String floorId = "F1";
        FloorAvailabilityDTO expectedDto = new FloorAvailabilityDTO();
        when(parkingRepository.getFloorAvailability(buildingId, floorId)).thenReturn(expectedDto);

        // Act
        FloorAvailabilityDTO result = parkingService.getFloorAvailability(buildingId, floorId);

        // Assert
        assertNotNull(result);
        verify(parkingRepository).getFloorAvailability(buildingId, floorId);
    }

    /**
     * Test for checking parking capacity by building and floor
     */
    @Test
    void checkCapacity_ShouldReturnCapacityForAllBuildingsAndFloors() {
        // Arrange
        Map<String, Building> buildings = new HashMap<>();
        
        // Create test building
        Building building = new Building();
        building.setBuildingId("B1");
        Floor floor = new Floor();
        floor.setBuildingId("B1");
        floor.setFloorId("F1");
        
        // Add some test slots to the floor
        ParkingSlot twoWheelerSlot = new ParkingSlot();
        twoWheelerSlot.setVehicleType(VehicleType.TWO_WHEELER);
        twoWheelerSlot.setOccupied(false);
        
        ParkingSlot fourWheelerSlot = new ParkingSlot();
        fourWheelerSlot.setVehicleType(VehicleType.FOUR_WHEELER);
        fourWheelerSlot.setOccupied(false);
        
        floor.getParkingSlots().add(twoWheelerSlot);
        floor.getParkingSlots().add(fourWheelerSlot);
        building.getFloors().add(floor);
        buildings.put("B1", building);
        
        when(parkingRepository.getAllBuildings()).thenReturn(buildings);

        // Act
        List<BuildingCapacityDTO> result = parkingService.checkCapacity();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        BuildingCapacityDTO buildingCapacity = result.get(0);
        assertEquals("B1", buildingCapacity.getBuildingId());
        assertEquals(1, buildingCapacity.getFloors().size());
        
        BuildingCapacityDTO.FloorCapacityDTO floorCapacity = buildingCapacity.getFloors().get(0);
        assertEquals("F1", floorCapacity.getFloorId());
        assertEquals(1, floorCapacity.getAvailableSlots().get(VehicleType.TWO_WHEELER));
        assertEquals(1, floorCapacity.getAvailableSlots().get(VehicleType.FOUR_WHEELER));
        
        verify(parkingRepository).getAllBuildings();
    }

    // Helper methods to create test data
    @SuppressWarnings("unused")
    private Map<String, Building> createTestBuildingData() {
        Map<String, Building> buildings = new HashMap<>();
        Building building = new Building();
        building.setBuildingId("B1");
        
        Floor floor1 = createTestFloor("B1", "F1");
        Floor floor2 = createTestFloor("B1", "F2");
        
        building.setFloors(Arrays.asList(floor1, floor2));
        buildings.put("B1", building);
        
        return buildings;
    }

    private Floor createTestFloor(String buildingId, String floorId) {
        Floor floor = new Floor();
        floor.setBuildingId(buildingId);
        floor.setFloorId(floorId);
        floor.setParkingSlots(new ArrayList<>());
        
        // Add some test slots
        floor.getParkingSlots().add(createTestParkingSlot(
            buildingId + "-" + floorId + "-TW-01", 
            false
        ));
        
        return floor;
    }

    private ParkingSlot createTestParkingSlot(String id, boolean occupied) {
        ParkingSlot slot = new ParkingSlot();
        slot.setId(id);
        slot.setOccupied(occupied);
        slot.setVehicleType(VehicleType.TWO_WHEELER);
        return slot;
    }

    @SuppressWarnings("unused")
    private ParkingRequestDTO createParkingRequest() {
        ParkingRequestDTO request = new ParkingRequestDTO();
        request.setVehicleType(VehicleType.TWO_WHEELER);
        request.setRegistrationNumber("KA01AB1234");
        return request;
    }
} 