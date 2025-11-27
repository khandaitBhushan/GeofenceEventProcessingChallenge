package com.geofence.service;


import com.geofence.entity.GeoZone;
import com.geofence.repository.GeoZoneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneInitializationService {

    private static final Logger log = LoggerFactory.getLogger(ZoneInitializationService.class);
    private final GeoZoneRepository zoneRepository;

    @PostConstruct
    public void initializeZones() {
        if (zoneRepository.count() == 0) {
            log.info("Initializing geographic zones...");

            List<GeoZone> zones = Arrays.asList(
                    GeoZone.builder()
                            .zoneId("CPK")
                            .zoneName("Central Park")
                            .minLat(40.764)
                            .maxLat(40.800)
                            .minLng(-73.981)
                            .maxLng(-73.949)
                            .build(),
                    GeoZone.builder()
                            .zoneId("TMS")
                            .zoneName("Times Square")
                            .minLat(40.755)
                            .maxLat(40.760)
                            .minLng(-73.986)
                            .maxLng(-73.982)
                            .build(),
                    GeoZone.builder()
                            .zoneId("LGA")
                            .zoneName("LaGuardia Airport")
                            .minLat(40.770)
                            .maxLat(40.780)
                            .minLng(-73.880)
                            .maxLng(-73.860)
                            .build(),
                    GeoZone.builder()
                            .zoneId("JFK")
                            .zoneName("JFK Airport")
                            .minLat(40.640)
                            .maxLat(40.650)
                            .minLng(-73.790)
                            .maxLng(-73.760)
                            .build()
            );

            zoneRepository.saveAll(zones);
            log.info("Successfully initialized {} geographic zones", zones.size());
        } else {
            log.info("Zones already initialized. Found {} zones.", zoneRepository.count());
        }
    }
}
