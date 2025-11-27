package com.geofence.repository;

import com.geofence.entity.GeoZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoZoneRepository extends JpaRepository<GeoZone, String> {
    @Query("SELECT z FROM GeoZone z WHERE :lat BETWEEN z.minLat AND z.maxLat AND :lng BETWEEN z.minLng AND z.maxLng")
    List<GeoZone> findZonesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
}