package com.example.parking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * 
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends BaseEntity {
    private String registrationNumber;
    private VehicleType type;
    
    @Override
    public String getIdentifier() {
        return registrationNumber;
    }
} 