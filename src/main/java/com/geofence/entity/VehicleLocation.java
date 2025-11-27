package com.geofence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLocation {
    @Id
    private String vehicleId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String currentZoneId;

    @CreationTimestamp
    private LocalDateTime lastUpdate;
}