package com.geofence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "geo_zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoZone {
    @Id
    private String zoneId;

    @Column(nullable = false)
    private String zoneName;

    @Column(nullable = false)
    private Double minLat;

    @Column(nullable = false)
    private Double maxLat;

    @Column(nullable = false)
    private Double minLng;

    @Column(nullable = false)
    private Double maxLng;
}