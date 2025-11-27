package com.geofence.repository;

import com.geofence.entity.ZoneTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneTransitionRepository extends JpaRepository<ZoneTransition, Long> {
    List<ZoneTransition> findByVehicleIdOrderByTimestampDesc(String vehicleId);
}