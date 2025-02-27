package com.example.parking.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.parking.dto.FloorAvailabilityDTO;
import com.example.parking.entity.BaseEntity;
import com.example.parking.entity.Building;
import com.example.parking.entity.Floor;
import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.ParkingStructure;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.VehicleType;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * In-memory implementation of parking repository.
 * Manages parking data structures and provides CRUD operations.
 * Initializes with sample data and supports dynamic updates.
 */
@Repository
@Slf4j
public class InMemoryParkingRepository implements ParkingRepository {
    private final Map<String, Building> buildings = new HashMap<>();
    private final Map<String, BaseEntity> entities = new HashMap<>();

    /**
     * Initializes parking infrastructure with sample data
     * Creates buildings, floors, and parking slots
     * Randomly occupies some slots for realistic simulation
     */
    @PostConstruct
    public void init() {
        // Initialize buildings and floors
        for (int buildingNum = 1; buildingNum <= 4; buildingNum++) {
            Building building = new Building();
            String buildingId = "B" + buildingNum;
            building.setBuildingId(buildingId);
            building.setId(buildingId);
            
            for (int floorNum = 1; floorNum <= 2; floorNum++) {
                Floor floor = new Floor();
                String floorId = "F" + floorNum;
                floor.setFloorId(floorId);
                floor.setBuildingId(buildingId);
                floor.setId(buildingId + "-" + floorId);
                
                // Initialize parking slots
                initializeParkingSlots(floor, buildingId, floorId);
                
                building.getFloors().add(floor);
                entities.put(floor.getId(), floor);
            }
            
            buildings.put(buildingId, building);
            entities.put(building.getId(), building);
        }
        
        populateRandomSlots();
    }

    /**
     * Creates parking slots for a floor
     * Generates unique IDs and initializes slot properties
     * @param floor Floor to add slots to
     * @param buildingId Parent building identifier
     * @param floorId Floor identifier
     */
    private void initializeParkingSlots(Floor floor, String buildingId, String floorId) {
        // Create TWO_WHEELER slots
        for (int i = 1; i <= 50; i++) {
            ParkingSlot slot = createParkingSlot(buildingId, floorId, "TW", i, VehicleType.TWO_WHEELER);
            floor.getParkingSlots().add(slot);
            entities.put(slot.getId(), slot);
        }
        
        // Create FOUR_WHEELER slots
        for (int i = 1; i <= 30; i++) {
            ParkingSlot slot = createParkingSlot(buildingId, floorId, "FW", i, VehicleType.FOUR_WHEELER);
            floor.getParkingSlots().add(slot);
            entities.put(slot.getId(), slot);
        }
    }

    private ParkingSlot createParkingSlot(String buildingId, String floorId, String prefix, int number, VehicleType type) {
        ParkingSlot slot = new ParkingSlot();
        slot.setId(String.format("%s-%s-%s-%02d", buildingId, floorId, prefix, number));
        slot.setBuildingId(buildingId);
        slot.setFloorId(floorId);
        slot.setVehicleType(type);
        slot.setOccupied(false);
        return slot;
    }

    @Override
    public <T extends BaseEntity> T save(T entity) {
        entities.put(entity.getId(), entity);
        if (entity instanceof Building building) {
            buildings.put(building.getBuildingId(), building);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> Optional<T> findById(String id, Class<T> entityClass) {
        return Optional.ofNullable((T) entities.get(id));
    }

    @Override
    public List<ParkingSpace> findAvailableSpaces(VehicleType vehicleType) {
        return entities.values().stream()
            .filter(e -> e instanceof ParkingSlot)
            .map(e -> (ParkingSlot) e)
            .filter(slot -> slot.getVehicleType() == vehicleType && !slot.isOccupied())
            .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ParkingStructure> List<T> findAllStructures(Class<T> structureClass) {
        return entities.values().stream()
            .filter(e -> structureClass.isInstance(e))
            .map(e -> (T) e)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Building> getAllBuildings() {
        return new HashMap<>(buildings);
    }

    @Override
    public List<ParkingSpace> findSpacesByStructure(String buildingId, String floorId) {
        return entities.values().stream()
            .filter(e -> e instanceof ParkingSlot)
            .map(e -> (ParkingSlot) e)
            .filter(slot -> slot.getBuildingId().equals(buildingId) 
                && slot.getFloorId().equals(floorId))
            .collect(Collectors.toList());
    }

    private void populateRandomSlots() {
        Random random = new Random();
        
        // Generate a random number between 5 and 30 (inclusive)
        int randomNumberTwoWheeler = 5 + random.nextInt(30);
        int randomNumberFourWheeler = 5 + random.nextInt(15);
        buildings.values().forEach(building -> 
            building.getFloors().forEach(floor -> {
                // Randomly occupy between 5 and 30 TWO_WHEELER slots
                List<ParkingSlot> twoWheelerSlots = floor.getParkingSlots().stream()
                    .filter(slot -> slot.getVehicleType() == VehicleType.TWO_WHEELER)
                    .collect(Collectors.toList());
                Collections.shuffle(twoWheelerSlots);
                twoWheelerSlots.subList(0, randomNumberTwoWheeler).forEach(slot -> {
                    slot.setOccupied(true);
                    Vehicle vehicle = new Vehicle();
                    vehicle.setRegistrationNumber("TW-" + UUID.randomUUID().toString().substring(0, 8));
                    vehicle.setType(VehicleType.TWO_WHEELER);
                    slot.setParkedVehicle(vehicle);
                });

                // Randomly occupy between 5 and 15 FOUR_WHEELER slots
                List<ParkingSlot> fourWheelerSlots = floor.getParkingSlots().stream()
                    .filter(slot -> slot.getVehicleType() == VehicleType.FOUR_WHEELER)
                    .collect(Collectors.toList());
                Collections.shuffle(fourWheelerSlots);
                fourWheelerSlots.subList(0, randomNumberFourWheeler).forEach(slot -> {
                    slot.setOccupied(true);
                    Vehicle vehicle = new Vehicle();
                    vehicle.setRegistrationNumber("FW-" + UUID.randomUUID().toString().substring(0, 8));
                    vehicle.setType(VehicleType.FOUR_WHEELER);
                    slot.setParkedVehicle(vehicle);
                });
            })
        );
    }

    @Override
    public List<ParkingSlot> getAllSlots() {
        return buildings.values().stream()
            .flatMap(building -> building.getFloors().stream())
            .flatMap(floor -> floor.getParkingSlots().stream())
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ParkingSlot> findById(String id) {
        return getAllSlots().stream()
            .filter(slot -> slot.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<ParkingSlot> findAvailableSlots(VehicleType vehicleType) {
        return getAllSlots().stream()
            .filter(slot -> slot.getVehicleType() == vehicleType && !slot.isOccupied())
            .collect(Collectors.toList());
    }

    @Override
    public FloorAvailabilityDTO getFloorAvailability(String buildingId, String floorId) {
        Optional<Floor> floor = findAllStructures(Floor.class).stream()
            .filter(f -> f.getBuildingId().equals(buildingId) && f.getFloorId().equals(floorId))
            .findFirst();

        if (floor.isEmpty()) {
            return null;
        }

        FloorAvailabilityDTO dto = new FloorAvailabilityDTO();
        dto.setBuildingId(buildingId);
        dto.setFloorId(floorId);
        dto.setAvailableTwoWheelerSlots(floor.get().getParkingSlots().stream()
            .filter(slot -> slot.getVehicleType() == VehicleType.TWO_WHEELER && !slot.isOccupied())
            .map(ParkingSlot::getId)
            .collect(Collectors.toList()));
        dto.setAvailableFourWheelerSlots(floor.get().getParkingSlots().stream()
            .filter(slot -> slot.getVehicleType() == VehicleType.FOUR_WHEELER && !slot.isOccupied())
            .map(ParkingSlot::getId)
            .collect(Collectors.toList()));
        dto.setTotalAvailableTwoWheelerSlots(dto.getAvailableTwoWheelerSlots().size());
        dto.setTotalAvailableFourWheelerSlots(dto.getAvailableFourWheelerSlots().size());
        
        return dto;
    }

    @Override
    public void updateSlot(ParkingSlot slot) {
        Building building = buildings.get(slot.getBuildingId());
        if (building != null) {
            building.getFloors().stream()
                .filter(floor -> floor.getFloorId().equals(slot.getFloorId()))
                .findFirst()
                .ifPresent(floor -> {
                    int index = floor.getParkingSlots().indexOf(slot);
                    if (index != -1) {
                        floor.getParkingSlots().set(index, slot);
                    }
                });
        }
    }
} 