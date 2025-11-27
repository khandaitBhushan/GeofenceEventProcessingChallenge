package com.geofence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleZoneStatus {
    private String vehicleId;
    private String currentZoneId;
    private String currentZoneName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdate;
    private String status;
}