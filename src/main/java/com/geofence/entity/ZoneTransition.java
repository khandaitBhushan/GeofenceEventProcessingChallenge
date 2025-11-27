package com.geofence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "zone_transitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneTransition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleId;

    private String zoneId;

    @Enumerated(EnumType.STRING)
    private TransitionType transitionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Double latitude;
    private Double longitude;
}