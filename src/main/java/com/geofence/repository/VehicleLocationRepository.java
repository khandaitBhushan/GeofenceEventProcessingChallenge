package com.geofence.repository;

import com.geofence.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, String> {
    Optional<VehicleLocation> findByVehicleId(String vehicleId);
}