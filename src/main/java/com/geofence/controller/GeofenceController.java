package com.geofence.controller;

import com.geofence.dto.ApiResponse;
import com.geofence.dto.LocationEvent;
import com.geofence.dto.VehicleZoneStatus;
import com.geofence.entity.ZoneTransition;
import com.geofence.service.GeofenceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class GeofenceController {

    private static final Logger log = LoggerFactory.getLogger(GeofenceController.class);
    private final GeofenceService geofenceService;

    public GeofenceController(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    @PostMapping("/location-events")
    public ResponseEntity<ApiResponse<String>> receiveLocationEvent(
            @Valid @RequestBody LocationEvent event) {

        log.info("Received location event for vehicle: {}", event.getVehicleId());

        // Set timestamp if not provided
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        geofenceService.processLocationEvent(event);

        return ResponseEntity.ok(ApiResponse.success("Location event processed successfully"));
    }

    @GetMapping("/vehicles/{vehicleId}/current-zone")
    public ResponseEntity<ApiResponse<VehicleZoneStatus>> getCurrentZone(
            @PathVariable String vehicleId) {

        log.info("Fetching current zone for vehicle: {}", vehicleId);

        VehicleZoneStatus status = geofenceService.getCurrentZoneStatus(vehicleId);

        return ResponseEntity.ok(ApiResponse.success("Current zone status retrieved", status));
    }

    @GetMapping("/vehicles/{vehicleId}/transition-history")
    public ResponseEntity<ApiResponse<List<ZoneTransition>>> getTransitionHistory(
            @PathVariable String vehicleId) {

        log.info("Fetching transition history for vehicle: {}", vehicleId);

        List<ZoneTransition> history = geofenceService.getTransitionHistory(vehicleId);

        return ResponseEntity.ok(ApiResponse.success("Transition history retrieved", history));
    }
}