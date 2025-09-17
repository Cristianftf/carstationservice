package com.station.carstationservice.repository;

import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {

    /**
     * Encuentra estaciones de carga por tipo de cargador
     */
    List<ChargingStation> findByChargerType(ChargerType chargerType);

    /**
     * Encuentra estaciones de carga por estado
     */
    List<ChargingStation> findByStatus(StationStatus status);

    /**
     * Encuentra estaciones de carga por ubicación aproximada (rango de coordenadas)
     */
    @Query("SELECT cs FROM ChargingStation cs WHERE cs.latitude BETWEEN :minLat AND :maxLat AND cs.longitude BETWEEN :minLon AND :maxLon")
    List<ChargingStation> findByLocationWithinRange(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon);

    /**
     * Encuentra estaciones de carga con un número mínimo de puntos de carga
     */
    List<ChargingStation> findByChargingPointsGreaterThanEqual(Integer minPoints);

    /**
     * Encuentra estaciones de carga por dirección (búsqueda parcial)
     */
    List<ChargingStation> findByAddressContainingIgnoreCase(String addressPart);

    /**
     * Verifica si existe una estación con la misma dirección
     */
    boolean existsByAddress(String address);

    /**
     * Cuenta estaciones de carga por estado
     */
    long countByStatus(StationStatus status);

    /**
     * Encuentra la primera estación disponible
     */
    Optional<ChargingStation> findFirstByStatusOrderByIdAsc(StationStatus status);
}