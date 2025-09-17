package com.station.carstationservice.service;

import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import com.station.carstationservice.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;

    /**
     * Obtiene todas las estaciones de carga
     */
    public List<ChargingStation> getAllStations() {
        log.info("Obteniendo todas las estaciones de carga");
        return chargingStationRepository.findAll();
    }

    /**
     * Obtiene estaciones de carga paginadas
     */
    public Page<ChargingStation> getAllStations(Pageable pageable) {
        log.info("Obteniendo estaciones de carga paginadas: {}", pageable);
        return chargingStationRepository.findAll(pageable);
    }

    /**
     * Obtiene una estación de carga por ID
     */
    public Optional<ChargingStation> getStationById(Long id) {
        log.info("Buscando estación de carga con ID: {}", id);
        return chargingStationRepository.findById(id);
    }

    /**
     * Crea una nueva estación de carga
     */
    @Transactional
    public ChargingStation createStation(ChargingStation station) {
        log.info("Creando nueva estación de carga: {}", station);
        
        // Validar que no exista una estación con la misma dirección
        if (chargingStationRepository.existsByAddress(station.getAddress())) {
            throw new IllegalArgumentException("Ya existe una estación con la misma dirección");
        }
        
        return chargingStationRepository.save(station);
    }

    /**
     * Actualiza una estación de carga existente
     */
    @Transactional
    public ChargingStation updateStation(Long id, ChargingStation stationDetails) {
        log.info("Actualizando estación de carga con ID: {}", id);
        
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada con ID: " + id));

        // Validar dirección única (si cambió la dirección)
        if (!station.getAddress().equals(stationDetails.getAddress()) && 
            chargingStationRepository.existsByAddress(stationDetails.getAddress())) {
            throw new IllegalArgumentException("Ya existe una estación con la misma dirección");
        }

        station.setAddress(stationDetails.getAddress());
        station.setLatitude(stationDetails.getLatitude());
        station.setLongitude(stationDetails.getLongitude());
        station.setChargerType(stationDetails.getChargerType());
        station.setChargingPoints(stationDetails.getChargingPoints());
        station.setStatus(stationDetails.getStatus());

        return chargingStationRepository.save(station);
    }

    /**
     * Elimina una estación de carga
     */
    @Transactional
    public void deleteStation(Long id) {
        log.info("Eliminando estación de carga con ID: {}", id);
        
        if (!chargingStationRepository.existsById(id)) {
            throw new IllegalArgumentException("Estación no encontrada con ID: " + id);
        }
        
        chargingStationRepository.deleteById(id);
    }

    /**
     * Obtiene estaciones de carga por tipo de cargador
     */
    public List<ChargingStation> getStationsByChargerType(ChargerType chargerType) {
        log.info("Buscando estaciones con tipo de cargador: {}", chargerType);
        return chargingStationRepository.findByChargerType(chargerType);
    }

    /**
     * Obtiene estaciones de carga por estado
     */
    public List<ChargingStation> getStationsByStatus(StationStatus status) {
        log.info("Buscando estaciones con estado: {}", status);
        return chargingStationRepository.findByStatus(status);
    }

    /**
     * Obtiene estaciones de carga disponibles (con caché)
     */
    @Cacheable(value = "availableStations", key = "'all'")
    public List<ChargingStation> getAvailableStations() {
        log.info("Buscando estaciones disponibles (sin caché)");
        return chargingStationRepository.findByStatus(StationStatus.AVAILABLE);
    }

    /**
     * Obtiene estaciones de carga en uso
     */
    public List<ChargingStation> getInUseStations() {
        log.info("Buscando estaciones en uso");
        return chargingStationRepository.findByStatus(StationStatus.IN_USE);
    }

    /**
     * Busca estaciones de carga por ubicación aproximada
     */
    public List<ChargingStation> findStationsByLocationRange(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        log.info("Buscando estaciones en rango de ubicación: lat[{}-{}], lon[{}-{}]",
                minLat, maxLat, minLon, maxLon);
        return chargingStationRepository.findByLocationWithinRange(minLat, maxLat, minLon, maxLon);
    }

    /**
     * Busca estaciones de carga por dirección (búsqueda parcial)
     */
    public List<ChargingStation> searchStationsByAddress(String addressPart) {
        log.info("Buscando estaciones por dirección: {}", addressPart);
        return chargingStationRepository.findByAddressContainingIgnoreCase(addressPart);
    }

    /**
     * Obtiene estaciones con un número mínimo de puntos de carga
     */
    public List<ChargingStation> getStationsWithMinChargingPoints(Integer minPoints) {
        log.info("Buscando estaciones con al menos {} puntos de carga", minPoints);
        return chargingStationRepository.findByChargingPointsGreaterThanEqual(minPoints);
    }

    /**
     * Cambia el estado de una estación
     */
    @Transactional
    public ChargingStation changeStationStatus(Long id, StationStatus newStatus) {
        log.info("Cambiando estado de estación {} a {}", id, newStatus);
        
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada con ID: " + id));
        
        station.setStatus(newStatus);
        return chargingStationRepository.save(station);
    }

    /**
     * Obtiene estadísticas del sistema
     */
    public StationStatistics getStatistics() {
        log.info("Obteniendo estadísticas del sistema");
        
        long totalStations = chargingStationRepository.count();
        long availableStations = chargingStationRepository.countByStatus(StationStatus.AVAILABLE);
        long inUseStations = chargingStationRepository.countByStatus(StationStatus.IN_USE);
        
        return new StationStatistics(totalStations, availableStations, inUseStations);
    }

    /**
     * Clase interna para estadísticas
     */
    public record StationStatistics(long totalStations, long availableStations, long inUseStations) {
        public double getAvailabilityPercentage() {
            return totalStations > 0 ? (availableStations * 100.0) / totalStations : 0.0;
        }
        
        public double getUsagePercentage() {
            return totalStations > 0 ? (inUseStations * 100.0) / totalStations : 0.0;
        }
    }
}