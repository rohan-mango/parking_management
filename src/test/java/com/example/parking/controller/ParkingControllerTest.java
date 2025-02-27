package com.example.parking.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.parking.dto.BuildingCapacityDTO;
import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.dto.FloorAvailabilityRequestDTO;
import com.example.parking.dto.ParkingRequestDTO;
import com.example.parking.dto.ParkingResponseDTO;
import com.example.parking.entity.VehicleType;
import com.example.parking.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Test class for ParkingController
 * Tests all REST endpoints and their functionality
 */
@WebMvcTest(ParkingController.class)
@ExtendWith(SpringExtension.class)
class ParkingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ParkingService parkingService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for checking parking capacity endpoint
     */
    @Test
    void checkCapacity_ShouldReturnCapacityByBuildingAndFloor() throws Exception {
        // Arrange
        List<BuildingCapacityDTO> capacityList = new ArrayList<>();
        
        // Create test data for Building 1
        BuildingCapacityDTO building1 = new BuildingCapacityDTO();
        building1.setBuildingId("B1");
        building1.setFloors(new ArrayList<>());

        // Add floors for Building 1
        BuildingCapacityDTO.FloorCapacityDTO floor1 = new BuildingCapacityDTO.FloorCapacityDTO();
        floor1.setFloorId("F1");
        Map<VehicleType, Integer> availableSlots1 = new EnumMap<>(VehicleType.class);
        availableSlots1.put(VehicleType.TWO_WHEELER, 30);
        availableSlots1.put(VehicleType.FOUR_WHEELER, 18);
        floor1.setAvailableSlots(availableSlots1);
        building1.getFloors().add(floor1);

        capacityList.add(building1);
        
        when(parkingService.checkCapacity()).thenReturn(capacityList);

        // Act & Assert
        mockMvc.perform(get("/api/parking/capacity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buildingId").value("B1"))
                .andExpect(jsonPath("$[0].floors[0].floorId").value("F1"))
                .andExpect(jsonPath("$[0].floors[0].availableSlots.TWO_WHEELER").value(30))
                .andExpect(jsonPath("$[0].floors[0].availableSlots.FOUR_WHEELER").value(18));
    }

    /**
     * Test for checking slot status endpoint
     */
    @Test
    void checkSlotStatus_ShouldReturnStatus() throws Exception {
        // Arrange
        String slotId = "B1-F1-TW-01";
        ParkingResponseDTO response = new ParkingResponseDTO();
        response.setSlotId(slotId);
        response.setSuccess(true);
        response.setMessage("Available");
        when(parkingService.checkSlotStatus(slotId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/parking/slot/{slotId}", slotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(slotId))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Available"));
    }

    /**
     * Test for parking vehicle endpoint
     */
    @Test
    void parkVehicle_ShouldParkSuccessfully() throws Exception {
        // Arrange
        ParkingRequestDTO request = new ParkingRequestDTO();
        request.setRegistrationNumber("KA01AB1234");
        request.setVehicleType(VehicleType.FOUR_WHEELER);

        ParkingResponseDTO response = new ParkingResponseDTO();
        response.setSlotId("B1-F1-FW-01");
        response.setSuccess(true);
        response.setMessage("Vehicle parked successfully");

        when(parkingService.parkVehicle(any(ParkingRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/parking/park")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vehicle parked successfully"));
    }

    /**
     * Test for floor availability endpoint
     */
    @Test
    void getFloorAvailability_ShouldReturnAvailability() throws Exception {
        // Arrange
        FloorAvailabilityRequestDTO request = new FloorAvailabilityRequestDTO();
        request.setBuildingId("B1");
        request.setFloorId("F1");

        FloorAvailabilityDTO response = new FloorAvailabilityDTO();
        response.setBuildingId("B1");
        response.setFloorId("F1");
        response.setAvailableTwoWheelerSlots(Arrays.asList("B1-F1-TW-01", "B1-F1-TW-02"));
        response.setAvailableFourWheelerSlots(Arrays.asList("B1-F1-FW-01"));
        response.setTotalAvailableTwoWheelerSlots(2);
        response.setTotalAvailableFourWheelerSlots(1);

        when(parkingService.getFloorAvailability("B1", "F1")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/parking/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingId").value("B1"))
                .andExpect(jsonPath("$.floorId").value("F1"))
                .andExpect(jsonPath("$.availableTwoWheelerSlots").isArray())
                .andExpect(jsonPath("$.totalAvailableTwoWheelerSlots").value(2))
                .andExpect(jsonPath("$.totalAvailableFourWheelerSlots").value(1));
    }

    /**
     * Test for floor availability endpoint when floor not found
     */
    @Test
    void getFloorAvailability_WhenFloorNotFound_ShouldReturn404() throws Exception {
        // Arrange
        FloorAvailabilityRequestDTO request = new FloorAvailabilityRequestDTO();
        request.setBuildingId("B999");
        request.setFloorId("F999");

        when(parkingService.getFloorAvailability("B999", "F999")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/parking/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
} 