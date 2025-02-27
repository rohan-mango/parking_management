package com.example.parking.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO for floor-wise parking availability information
 */
@Data
public class FloorAvailabilityDTO {
    private String buildingId;
    private String floorId;
    private List<String> availableTwoWheelerSlots;
    private List<String> availableFourWheelerSlots;
    private int totalAvailableTwoWheelerSlots;
    private int totalAvailableFourWheelerSlots;
} 