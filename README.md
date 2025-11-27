# Geofence Event Processing Service
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/khandaitBhushan/GeofenceEventProcessingChallenge)

This repository contains a Spring Boot application designed to process vehicle location events and determine their relationship with predefined geographic zones (geofences). The service can track when vehicles enter or exit these zones, retrieve the current status of a vehicle, and provide a history of its zone transitions.

The application is built to be easily run locally for development (using MySQL) or deployed as a containerized service (using PostgreSQL).

## Features

*   **Process Location Events**: Ingests real-time vehicle location data (latitude, longitude).
*   **Geofence Detection**: Determines if a vehicle's location falls within any of the predefined geographic zones.
*   **Transition Tracking**: Records events when a vehicle enters or exits a geofence.
*   **Current Status Query**: Provides the last known location and current zone status for any vehicle.
*   **Historical Data**: Retrieves a complete history of all geofence transitions for a specific vehicle.
*   **Zone Initialization**: Automatically populates the database with predefined zones for key New York City locations (Central Park, Times Square, LaGuardia Airport, JFK Airport) on first run.

## Technology Stack

*   **Backend**: Java 17, Spring Boot 3, Spring Data JPA
*   **Databases**: PostgreSQL (Production), MySQL (Development)
*   **Build Tool**: Apache Maven
*   **Containerization**: Docker, Docker Compose
*   **Deployment**: Configuration provided for [Render](https://render.com)

## API Endpoints

The API base path for all endpoints is `/api/v1`.

### 1. Submit a Location Event

Processes a new location update for a vehicle. It determines if this update results in an entrance to, or exit from, a geofence.

*   **Endpoint**: `POST /location-events`
*   **Request Body**:

    ```json
    {
        "vehicleId": "taxi-101",
        "latitude": 40.7580,
        "longitude": -73.9855,
        "timestamp": "2024-01-28T10:00:00Z"
    }
    ```
    *Note: If `timestamp` is omitted, the server's current time will be used.*

*   **Success Response** (200 OK):

    ```json
    {
        "success": true,
        "message": "Location event processed successfully",
        "data": null,
        "timestamp": "2024-01-28T10:00:01.12345Z"
    }
    ```

### 2. Get Current Vehicle Zone Status

Retrieves the last recorded location and current zone information for a specific vehicle.

*   **Endpoint**: `GET /vehicles/{vehicleId}/current-zone`
*   **Example URL**: `/api/v1/vehicles/taxi-101/current-zone`
*   **Success Response** (200 OK):

    ```json
    {
        "success": true,
        "message": "Current zone status retrieved",
        "data": {
            "vehicleId": "taxi-101",
            "currentZoneId": "TMS",
            "currentZoneName": "Times Square",
            "latitude": 40.7580,
            "longitude": -73.9855,
            "lastUpdate": "2024-01-28T10:00:01.123Z",
            "status": "IN_ZONE"
        },
        "timestamp": "2024-01-28T10:05:00.678Z"
    }
    ```
    *`status` can be `IN_ZONE`, `NO_ZONE`, or `VEHICLE_NOT_FOUND`.*

### 3. Get Vehicle Transition History

Retrieves a list of all recorded zone enter/exit events for a specific vehicle, sorted by the most recent first.

*   **Endpoint**: `GET /vehicles/{vehicleId}/transition-history`
*   **Example URL**: `/api/v1/vehicles/taxi-101/transition-history`
*   **Success Response** (200 OK):

    ```json
    {
        "success": true,
        "message": "Transition history retrieved",
        "data": [
            {
                "id": 2,
                "vehicleId": "taxi-101",
                "zoneId": "TMS",
                "transitionType": "ENTER",
                "timestamp": "2024-01-28T10:00:00Z",
                "latitude": 40.7580,
                "longitude": -73.9855
            },
            {
                "id": 1,
                "vehicleId": "taxi-101",
                "zoneId": "CPK",
                "transitionType": "EXIT",
                "timestamp": "2024-01-28T09:45:00Z",
                "latitude": 40.763,
                "longitude": -73.982
            }
        ],
        "timestamp": "2024-01-28T10:06:00.910Z"
    }
    ```

## Getting Started

### Prerequisites

*   Java 17 or higher
*   Apache Maven
*   Docker and Docker Compose

### Option 1: Run Locally with Docker (Recommended)

This method uses Docker Compose to start the application and a PostgreSQL database, mirroring the production environment.

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/khandaitbhushan/GeofenceEventProcessingChallenge.git
    cd GeofenceEventProcessingChallenge
    ```

2.  **Build and run with Docker Compose:**
    ```sh
    docker-compose up --build
    ```

The application will be accessible at `http://localhost:8080`.

### Option 2: Run Locally with Maven and MySQL

This method is suitable for development if you prefer to run the application directly on your machine and connect to a local MySQL instance.

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/khandaitbhushan/GeofenceEventProcessingChallenge.git
    cd GeofenceEventProcessingChallenge
    ```

2.  **Configure the database connection:**
    Open `src/main/resources/application-dev.properties` and update the MySQL connection details:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/geofence_db?createDatabaseIfNotExist=true
    spring.datasource.username=your-mysql-user
    spring.datasource.password=your-mysql-password
    ```

3.  **Run the application:**
    ```sh
    ./mvnw spring-boot:run
    ```

The application will be accessible at `http://localhost:8080`. The `dev` profile is active by default.

## Deployment

This service is configured for easy deployment on **Render** using the `render.yaml` blueprint.

*   The service is built and deployed as a Docker container from the provided `Dockerfile`.
*   It automatically provisions and connects to a free-tier PostgreSQL database named `geofence-db`.
*   The `prod` Spring profile is activated, which configures JPA for PostgreSQL.
*   A health check is configured at `/actuator/health` to ensure service reliability.
*   Auto-deploy is enabled, meaning any push to the main branch will trigger a new deployment.
