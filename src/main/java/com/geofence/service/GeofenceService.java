package com.geofence.service;

import com.geofence.dto.LocationEvent;
import com.geofence.dto.VehicleZoneStatus;
import com.geofence.entity.GeoZone;
import com.geofence.entity.TransitionType;
import com.geofence.entity.VehicleLocation;
import com.geofence.entity.ZoneTransition;
import com.geofence.repository.GeoZoneRepository;
import com.geofence.repository.VehicleLocationRepository;
import com.geofence.repository.ZoneTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeofenceService {

    private static final Logger log = LoggerFactory.getLogger(GeofenceService.class);
    private final GeoZoneRepository zoneRepository;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final ZoneTransitionRepository transitionRepository;

    @Transactional
    public void processLocationEvent(LocationEvent event) {
        try {
            log.info("Processing location event for vehicle: {}", event.getVehicleId());

            validateCoordinates(event.getLatitude(), event.getLongitude());

            VehicleLocation currentState = getOrCreateVehicleState(event.getVehicleId());
            String previousZoneId = currentState.getCurrentZoneId();

            GeoZone currentZone = findZoneContainingPoint(event.getLatitude(), event.getLongitude());
            String currentZoneId = currentZone != null ? currentZone.getZoneId() : null;

            handleZoneTransitions(event, previousZoneId, currentZoneId);

            updateVehicleLocation(event, currentZoneId);

            log.info("Successfully processed event for vehicle: {}", event.getVehicleId());

        } catch (Exception e) {
            log.error("Error processing location event for vehicle: {}", event.getVehicleId(), e);
            throw new RuntimeException("Failed to process location event: " + e.getMessage());
        }
    }

    private void validateCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new IllegalArgumentException("Invalid coordinates: lat=" + lat + ", lng=" + lng);
        }
    }

    private VehicleLocation getOrCreateVehicleState(String vehicleId) {
        return vehicleLocationRepository.findByVehicleId(vehicleId)
                .orElse(VehicleLocation.builder()
                        .vehicleId(vehicleId)
                        .build());
    }

    private GeoZone findZoneContainingPoint(Double lat, Double lng) {
        log.debug("Searching for zones containing point: ({}, {})", lat, lng);

        List<GeoZone> zones = zoneRepository.findZonesContainingPoint(lat, lng);

        List<GeoZone> allZones = zoneRepository.findAll();
        log.debug("Total zones in database: {}", allZones.size());
        allZones.forEach(zone ->
                log.debug("Zone: {} - Lat[{}, {}], Lng[{}, {}]",
                        zone.getZoneName(), zone.getMinLat(), zone.getMaxLat(), zone.getMinLng(), zone.getMaxLng())
        );

        log.debug("Found {} zones containing the point", zones.size());

        return zones.isEmpty() ? null : zones.get(0);
    }

    private void handleZoneTransitions(LocationEvent event, String previousZoneId, String currentZoneId) {
        if (previousZoneId == null && currentZoneId != null) {
            saveTransition(event, currentZoneId, TransitionType.ENTER);
            log.info("Vehicle {} entered zone: {}", event.getVehicleId(), currentZoneId);

        } else if (previousZoneId != null && currentZoneId == null) {
            saveTransition(event, previousZoneId, TransitionType.EXIT);
            log.info("Vehicle {} exited zone: {}", event.getVehicleId(), previousZoneId);

        } else if (previousZoneId != null && currentZoneId != null &&
                !previousZoneId.equals(currentZoneId)) {
            saveTransition(event, previousZoneId, TransitionType.EXIT);
            saveTransition(event, currentZoneId, TransitionType.ENTER);
            log.info("Vehicle {} changed from zone {} to zone {}",
                    event.getVehicleId(), previousZoneId, currentZoneId);
        }
    }

    private void saveTransition(LocationEvent event, String zoneId, TransitionType type) {
        ZoneTransition transition = ZoneTransition.builder()
                .vehicleId(event.getVehicleId())
                .zoneId(zoneId)
                .transitionType(type)
                .timestamp(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .build();
        transitionRepository.save(transition);
    }

    private void updateVehicleLocation(LocationEvent event, String currentZoneId) {
        VehicleLocation updatedLocation = VehicleLocation.builder()
                .vehicleId(event.getVehicleId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .currentZoneId(currentZoneId)
                .lastUpdate(LocalDateTime.now())
                .build();
        vehicleLocationRepository.save(updatedLocation);
    }

    public VehicleZoneStatus getCurrentZoneStatus(String vehicleId) {
        Optional<VehicleLocation> locationOpt = vehicleLocationRepository.findByVehicleId(vehicleId);

        if (locationOpt.isEmpty()) {
            return VehicleZoneStatus.builder()
                    .vehicleId(vehicleId)
                    .status("VEHICLE_NOT_FOUND")
                    .build();
        }

        VehicleLocation location = locationOpt.get();
        String zoneName = null;
        if (location.getCurrentZoneId() != null) {
            Optional<GeoZone> zoneOpt = zoneRepository.findById(location.getCurrentZoneId());
            zoneName = zoneOpt.map(GeoZone::getZoneName).orElse("Unknown Zone");
        }

        return VehicleZoneStatus.builder()
                .vehicleId(vehicleId)
                .currentZoneId(location.getCurrentZoneId())
                .currentZoneName(zoneName)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .lastUpdate(location.getLastUpdate())
                .status(location.getCurrentZoneId() != null ? "IN_ZONE" : "NO_ZONE")
                .build();
    }

    public List<ZoneTransition> getTransitionHistory(String vehicleId) {
        return transitionRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
    }
}