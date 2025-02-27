# Parking Management System

A Spring Boot microservice for managing parking slots across multiple buildings. The system handles both two-wheeler and four-wheeler parking with real-time availability tracking.

## 🏗 System Architecture

The system is built following SOLID principles and implements various design patterns for maintainable, scalable code.

### Building Structure
- 4 Buildings (B1, B2, B3, B4)
- 2 Floors per building (F1, F2)
- Each floor contains:
  - 50 Two-wheeler slots
  - 30 Four-wheeler slots

## 🚀 API Endpoints

### 1. Check Parking Capacity

```bash
GET /api/parking/capacity
```

Returns available slots count for each vehicle type across all buildings.

**Sample Response:**

```json
{
  "TWO_WHEELER": 30,
  "FOUR_WHEELER": 20
}
```

### 2. Check Slot Status

```bash
GET /api/parking/slot/{slotId}
```

Returns the status of a specific parking slot.

**Sample Response:**

```json
{
  "slotId": "123",
  "isAvailable": false,
  "vehicleType": "FOUR_WHEELER"
}
```

### 3. Park Vehicle

```bash
POST /api/parking/park
```
Parks a vehicle in the parking system.

**Sample Request:**

```json
{
  "vehicleType": "FOUR_WHEELER",
  "vehicleNumber": "ABC123"
}
```

**Sample Response:**

```json
{
  "slotId": "123",
  "vehicleType": "FOUR_WHEELER"
}
```

### 4. Check Floor Availability

```bash
POST /api/parking/availability
```
Returns available slots in a specific building floor.

**Request Body:**

```json
{
  "buildingId": "B1",
  "floorId": "F1"
}
```

**Sample Response:**

```json
{
  "buildingId": "B1",
"floorId": "F1",
"availableTwoWheelerSlots": ["B1-F1-TW-01", "B1-F1-TW-02"],
"availableFourWheelerSlots": ["B1-F1-FW-01"],
"totalAvailableTwoWheelerSlots": 2,
"totalAvailableFourWheelerSlots": 1
}

```

## 🧪 Test Cases

### Test Data Configuration
- Each floor is initialized with random occupancy:
  - 20 Two-wheeler slots randomly occupied
  - 12 Four-wheeler slots randomly occupied
- Test data is generated in `InMemoryParkingRepository.java`

### Test Coverage
1. Controller Tests (`ParkingControllerTest.java`)
   - API endpoint validation
   - Request/Response format verification
   - Error handling scenarios

2. Service Tests (`ParkingServiceImplTest.java`)
   - Business logic validation
   - Edge cases handling
   - Data transformation verification

## 💡 Design Patterns & SOLID Principles

### SOLID Principles Implementation

1. **Single Responsibility Principle (SRP)**
   - Controllers handle only HTTP requests (`ParkingController.java`)
   - Services contain business logic (`ParkingServiceImpl.java`)
   - Repositories manage data access (`InMemoryParkingRepository.java`)

2. **Open/Closed Principle (OCP)**
   - Repository interface allows for different implementations
   - Easy to extend vehicle types without modifying existing code

3. **Interface Segregation Principle (ISP)**
   - Focused interfaces (`ParkingService.java`, `ParkingRepository.java`)
   - Clear separation of concerns

4. **Dependency Inversion Principle (DIP)**
   - Dependencies injected through constructors
   - High-level modules depend on abstractions

### Design Patterns Used

1. **Repository Pattern**
   - `ParkingRepository.java` - Interface
   - `InMemoryParkingRepository.java` - Implementation
   - Separates data access logic

2. **DTO Pattern**
   - `ParkingRequestDTO.java`
   - `ParkingResponseDTO.java`
   - `FloorAvailabilityDTO.java`
   - Separates API contracts from domain models

3. **Builder Pattern**
   - Used in test cases for creating test objects
   - Simplifies object construction

4. **Factory Pattern**
   - Spring's dependency injection container
   - Bean creation and management

## 🛠 Technical Stack

- Java 17
- Spring Boot 3.x
- JUnit 5
- Mockito
- Maven
- Swagger/OpenAPI

## 📚 Documentation

API documentation is available through Swagger UI:

```bash
http://localhost:8080/swagger-ui.html
```

## 🔄 Project Structure
```
src/
├── main/java/com/example/parking/
│   ├── controller/
│   │   └── ParkingController.java
│   ├── service/
│   │   ├── ParkingService.java
│   │   └── ParkingServiceImpl.java
│   ├── repository/
│   │   ├── ParkingRepository.java
│   │   └── InMemoryParkingRepository.java
│   ├── entity/
│   │   ├── Building.java
│   │   ├── Floor.java
│   │   ├── ParkingSlot.java
│   │   └── VehicleType.java
│   └── dto/
│       ├── ParkingRequestDTO.java
│       ├── ParkingResponseDTO.java
│       └── FloorAvailabilityDTO.java
└── test/java/com/example/parking/
    ├── controller/
    │   └── ParkingControllerTest.java
    └── service/
        └── ParkingServiceImplTest.java
```