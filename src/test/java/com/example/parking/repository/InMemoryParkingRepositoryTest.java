package com.example.parking.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.entity.Building;
import com.example.parking.entity.Floor;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.VehicleType;

@ExtendWith(MockitoExtension.class)
class InMemoryParkingRepositoryTest {
    
    private InMemoryParkingRepository repository;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        repository = new InMemoryParkingRepository();
        initializeTestData();
    }

    private void initializeTestData() {
        Building building = new Building();
        building.setBuildingId("B1");

        Floor floor = new Floor();
        floor.setFloorId("F1");
        floor.setBuildingId("B1");

        // Add test parking slots
        ParkingSlot twoWheelerSlot = new ParkingSlot();
        twoWheelerSlot.setId("B1-F1-TW-01");
        twoWheelerSlot.setVehicleType(VehicleType.TWO_WHEELER);
        twoWheelerSlot.setOccupied(false);

        ParkingSlot fourWheelerSlot = new ParkingSlot();
        fourWheelerSlot.setId("B1-F1-FW-01");
        fourWheelerSlot.setVehicleType(VehicleType.FOUR_WHEELER);
        fourWheelerSlot.setOccupied(false);

        floor.getParkingSlots().add(twoWheelerSlot);
        floor.getParkingSlots().add(fourWheelerSlot);
        building.getFloors().add(floor);

        repository.save(building);
        repository.save(floor);
        repository.save(twoWheelerSlot);
        repository.save(fourWheelerSlot);
    }

    @Test
    @DisplayName("Should find all buildings with correct structure")
    void getAllBuildings_ShouldReturnAllBuildingsWithCorrectStructure() {
        // Act
        Map<String, Building> buildings = repository.getAllBuildings();

        // Assert
        assertAll(
            () -> assertEquals(1, buildings.size(), "Should have 1 building"),
            () -> assertTrue(buildings.containsKey("B1"), "Should contain Building 1"),
            () -> assertEquals(1, buildings.get("B1").getFloors().size(), "Building 1 should have 1 floor"),
            () -> assertEquals(2, buildings.get("B1").getFloors().get(0).getParkingSlots().size(), 
                "Floor 1 should have 2 total slots (1 two-wheeler + 1 four-wheeler)")
        );
    }

    @Test
    @DisplayName("Should find available slots by vehicle type")
    void findAvailableSlots_ShouldReturnCorrectSlots() {
        // Act
        List<ParkingSlot> twoWheelerSlots = repository.findAvailableSlots(VehicleType.TWO_WHEELER);
        List<ParkingSlot> fourWheelerSlots = repository.findAvailableSlots(VehicleType.FOUR_WHEELER);

        // Assert
        assertAll(
            () -> assertFalse(twoWheelerSlots.isEmpty(), "Should have available two-wheeler slots"),
            () -> assertFalse(fourWheelerSlots.isEmpty(), "Should have available four-wheeler slots"),
            () -> assertEquals(VehicleType.TWO_WHEELER, twoWheelerSlots.get(0).getVehicleType()),
            () -> assertEquals(VehicleType.FOUR_WHEELER, fourWheelerSlots.get(0).getVehicleType())
        );
    }

    @Test
    @DisplayName("Should find slot by ID")
    void findById_WhenSlotExists_ShouldReturnSlot() {
        // Arrange
        String slotId = "B1-F1-TW-01";

        // Act
        Optional<ParkingSlot> slot = repository.findById(slotId);

        // Assert
        assertAll(
            () -> assertTrue(slot.isPresent(), "Slot should be found"),
            () -> assertEquals(slotId, slot.get().getId(), "Should have correct ID"),
            () -> assertEquals(VehicleType.TWO_WHEELER, slot.get().getVehicleType(), 
                "Should be two-wheeler slot")
        );
    }

    @Test
    @DisplayName("Should update slot status")
    void updateSlot_ShouldUpdateSlotCorrectly() {
        // Arrange
        String slotId = "B1-F1-TW-01";
        ParkingSlot slot = repository.findById(slotId).get();
        slot.setOccupied(true);

        // Act
        repository.updateSlot(slot);

        // Assert
        ParkingSlot updatedSlot = repository.findById(slotId).get();
        assertTrue(updatedSlot.isOccupied(), "Slot should be marked as occupied");
    }

    @Test
    @DisplayName("Should get floor availability")
    void getFloorAvailability_ShouldReturnCorrectAvailability() {
        // Arrange
        String buildingId = "B1";
        String floorId = "F1";

        // Act
        FloorAvailabilityDTO availability = repository.getFloorAvailability(buildingId, floorId);

        // Assert
        assertAll(
            () -> assertNotNull(availability, "Availability should not be null"),
            () -> assertEquals(buildingId, availability.getBuildingId(), "Should have correct building ID"),
            () -> assertEquals(floorId, availability.getFloorId(), "Should have correct floor ID"),
            () -> assertTrue(availability.getTotalAvailableTwoWheelerSlots() > 0, 
                "Should have available two-wheeler slots"),
            () -> assertTrue(availability.getTotalAvailableFourWheelerSlots() > 0, 
                "Should have available four-wheeler slots")
        );
    }
} 