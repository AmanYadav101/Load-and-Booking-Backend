# Logistics Load Booking System

A Spring Boot application for managing logistics loads and bookings between shippers and transporters.

## Overview

This system facilitates the process of load creation by shippers and booking by transporters. It provides a complete API for managing the entire workflow of logistics operations, including creating, updating, and managing loads and bookings.

## Features

- **Load Management**: Create, read, update, and delete loads (shipments)
- **Booking Management**: Create, read, update, and delete bookings for loads
- **Status Tracking**: Track the status of loads (POSTED, BOOKED, CANCELLED) and bookings (PENDING, ACCEPTED, REJECTED)
- **Filtering**: Filter loads and bookings based on various parameters
- **Validation**: Request validation to ensure data integrity
- **Error Handling**: Comprehensive error handling with custom exceptions
- **Logging**: Detailed logging throughout the application

## Tech Stack

- **Java**: The primary programming language
- **Spring Boot**: The framework for creating stand-alone, production-grade Spring applications
- **Spring Data JPA**: For data persistence
- **Hibernate**: ORM for database operations
- **Jakarta Validation**: For request validation
- **SLF4J**: For logging
- **UUID**: For unique identification of resources

## API Endpoints

### Load API

- `POST /load`: Create a new load
- `GET /load`: Get all loads (with optional filtering)
- `GET /load/{loadId}`: Get a specific load by ID
- `PUT /load/{loadId}`: Update a specific load
- `DELETE /load/{loadId}`: Delete a specific load

### Booking API

- `POST /booking`: Create a new booking for a load
- `GET /booking`: Get all bookings (with optional filtering)
- `GET /booking/{bookingId}`: Get a specific booking by ID
- `PUT /booking/{bookingId}`: Update a specific booking
- `DELETE /booking/{bookingId}`: Delete a specific booking

## Filtering Capabilities

### Load Filtering

The GET `/load` endpoint supports the following filter parameters:

- `shipperId`: Filter loads by shipper ID
- `truckType`: Filter loads by truck type
- `status`: Filter loads by status (POSTED, BOOKED, CANCELLED)
- `loadingPoint`: Filter loads by loading point
- `unloadingPoint`: Filter loads by unloading point

Example:
```
GET /load?shipperId=SHIPPER123&truckType=Open&status=POSTED
```

Filter combinations are supported as follows:
- Single filters: Any parameter can be used individually
- Combined filters: Any combination of shipperId, truckType, and status
- Facility filters: loadingPoint and unloadingPoint can be combined with any other filter

### Booking Filtering

The GET `/booking` endpoint supports the following filter parameters:

- `transporterId`: Filter bookings by transporter ID
- `shipperId`: Filter bookings by the shipper ID associated with the load
- `status`: Filter bookings by status (PENDING, ACCEPTED, REJECTED)

Example:
```
GET /booking?transporterId=TRANS456&status=PENDING
```

All filters can be used individually or in combination.

## Assumptions

1. **User Authentication**: This API assumes that authentication and authorization are handled by a separate service. User IDs (shipperId, transporterId) are expected to be valid and pre-verified.

2. **Single Booking Per Load**: The system assumes that a load can have only one active booking at a time. When a booking is created, the load status is changed to BOOKED.

3. **Status Transitions**:
   - Loads can transition from POSTED → BOOKED → CANCELLED
   - Bookings can transition from PENDING → ACCEPTED/REJECTED
   - Manual status updates are allowed through the API

4. **Data Persistence**: The application assumes a relational database backend with transaction support.

5. **UUID Generation**: The system assumes that UUIDs are generated for all entities (loads and bookings) at creation time.

6. **Timestamp Handling**: All timestamps are expected to be provided in ISO format and are stored in the database's native timestamp format.

7. **Validation**: The system assumes that frontend applications will validate input before sending to the API, but it also performs its own validation.

8. **Error Handling**: The system assumes that clients can handle HTTP status codes and error messages appropriately.

9. **Scalability**: The current implementation assumes moderate load and doesn't implement caching or other performance optimizations.

10. **External Integration**: The system assumes no direct integration with external systems like payment processors, GPS tracking, or notification services.

## Data Models

### Load

A load represents a shipment that needs to be transported.

- `id`: UUID - Unique identifier for the load
- `shipperId`: String - ID of the shipper who created the load
- `facility`: Embedded object with loading and unloading details
- `productType`: String - Type of product being shipped
- `truckType`: String - Type of truck required
- `noOfTrucks`: int - Number of trucks required
- `weight`: double - Weight of the load
- `comment`: String - Additional comments
- `datePosted`: Timestamp - When the load was posted
- `status`: String - Current status (POSTED, BOOKED, CANCELLED)

### Facility

Embedded within a Load, contains pickup and delivery details.

- `loadingPoint`: String - Where the load will be picked up
- `unloadingPoint`: String - Where the load will be delivered
- `loadingDate`: Timestamp - When the load should be picked up
- `unloadingDate`: Timestamp - When the load should be delivered

### Booking

A booking represents a transporter's request to transport a load.

- `id`: UUID - Unique identifier for the booking
- `load`: Load - The load associated with this booking
- `transporterId`: String - ID of the transporter making the booking
- `proposedRate`: double - Rate proposed by the transporter
- `comment`: String - Additional comments
- `status`: String - Current status (PENDING, ACCEPTED, REJECTED)
- `requestedAt`: Timestamp - When the booking was requested

## Business Rules

1. A load can only be deleted if it has no active bookings
2. A booking cannot be created for a cancelled load
3. When a booking is created, the associated load's status is changed to BOOKED
4. When a booking is deleted, the associated load's status is changed to CANCELLED

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher
- A database server (MySQL, PostgreSQL, etc.)

### Configuration

Create an `application.properties` file in the `src/main/resources` directory:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/logistics
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Logging Configuration
logging.level.com.aman.booking=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Server Configuration
server.port=8080
```

### Running the Application

```bash
mvn spring-boot:run
```

## API Usage Examples

### Creating a Load

```bash
curl -X POST http://localhost:8080/load \
  -H "Content-Type: application/json" \
  -d '{
    "shipperId": "SHIPPER123",
    "facility": {
      "loadingPoint": "Delhi",
      "unloadingPoint": "Mumbai",
      "loadingDate": "2025-04-20T10:00:00",
      "unloadingDate": "2025-04-22T18:00:00"
    },
    "productType": "Electronics",
    "truckType": "Open",
    "noOfTrucks": 2,
    "weight": 5000,
    "comment": "Handle with care",
    "datePosted": "2025-04-16T09:00:00"
  }'
```

### Filtering Loads

```bash
# Get loads by shipper
curl -X GET "http://localhost:8080/load?shipperId=SHIPPER123"

# Get loads by truck type and status
curl -X GET "http://localhost:8080/load?truckType=Open&status=POSTED"

# Get loads by loading and unloading points
curl -X GET "http://localhost:8080/load?loadingPoint=Delhi&unloadingPoint=Mumbai"
```

### Creating a Booking

```bash
curl -X POST http://localhost:8080/booking \
  -H "Content-Type: application/json" \
  -d '{
    "loadId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "transporterId": "TRANS456",
    "proposedRate": 25000,
    "comment": "Can deliver early",
    "status": "PENDING",
    "requestedAt": "2025-04-16T11:30:00"
  }'
```

### Filtering Bookings

```bash
# Get bookings by transporter
curl -X GET "http://localhost:8080/booking?transporterId=TRANS456"

# Get bookings by status
curl -X GET "http://localhost:8080/booking?status=PENDING"

# Get bookings by shipper and status
curl -X GET "http://localhost:8080/booking?shipperId=SHIPPER123&status=ACCEPTED"
```

## Error Handling

The application handles various errors including:

- `ResourceNotFoundException`: When a requested resource doesn't exist
- `BusinessRuleViolationException`: When business rules are violated
- `InvalidDataException`: When invalid data is provided


## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with Spring Boot
- Inspired by logistics domain needs