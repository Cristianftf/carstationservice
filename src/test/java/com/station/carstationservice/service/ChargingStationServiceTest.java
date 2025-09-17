package com.station.carstationservice.service;

import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import com.station.carstationservice.repository.ChargingStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargingStationServiceTest {

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ChargingStationService chargingStationService;

    private ChargingStation availableStation;
    private ChargingStation inUseStation;

    @BeforeEach
    void setUp() {
        availableStation = ChargingStation.builder()
                .id(1L)
                .address("Calle Principal 123")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        inUseStation = ChargingStation.builder()
                .id(2L)
                .address("Avenida Secundaria 456")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.IN_USE)
                .build();

        when(cacheManager.getCache("availableStations")).thenReturn(cache);
    }

    @Test
    void testGetAllStations() {
        // Given
        when(chargingStationRepository.findAll()).thenReturn(List.of(availableStation, inUseStation));

        // When
        List<ChargingStation> result = chargingStationService.getAllStations();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(availableStation, inUseStation);
        verify(chargingStationRepository).findAll();
    }

    @Test
    void testGetAllStationsPaged() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChargingStation> page = new PageImpl<>(List.of(availableStation, inUseStation));
        when(chargingStationRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<ChargingStation> result = chargingStationService.getAllStations(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(availableStation, inUseStation);
        verify(chargingStationRepository).findAll(pageable);
    }

    @Test
    void testGetStationById_Found() {
        // Given
        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(availableStation));

        // When
        Optional<ChargingStation> result = chargingStationService.getStationById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(availableStation);
        verify(chargingStationRepository).findById(1L);
    }

    @Test
    void testGetStationById_NotFound() {
        // Given
        when(chargingStationRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<ChargingStation> result = chargingStationService.getStationById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(chargingStationRepository).findById(999L);
    }

    @Test
    void testCreateStation() {
        // Given
        ChargingStation newStation = ChargingStation.builder()
                .address("Nueva Calle 789")
                .latitude(40.7130)
                .longitude(-74.0062)
                .chargerType(ChargerType.AC)
                .chargingPoints(6)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation savedStation = ChargingStation.builder()
                .id(3L)
                .address("Nueva Calle 789")
                .latitude(40.7130)
                .longitude(-74.0062)
                .chargerType(ChargerType.AC)
                .chargingPoints(6)
                .status(StationStatus.AVAILABLE)
                .build();

        when(chargingStationRepository.save(newStation)).thenReturn(savedStation);

        // When
        ChargingStation result = chargingStationService.createStation(newStation);

        // Then
        assertThat(result).isEqualTo(savedStation);
        assertThat(result.getId()).isEqualTo(3L);
        verify(chargingStationRepository).save(newStation);
        verify(cache).clear(); // Cache should be cleared after creation
    }

    @Test
    void testUpdateStation_Exists() {
        // Given
        ChargingStation updateDetails = ChargingStation.builder()
                .address("Calle Actualizada 123")
                .latitude(40.7140)
                .longitude(-74.0070)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(10)
                .status(StationStatus.IN_USE)
                .build();

        ChargingStation updatedStation = ChargingStation.builder()
                .id(1L)
                .address("Calle Actualizada 123")
                .latitude(40.7140)
                .longitude(-74.0070)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(10)
                .status(StationStatus.IN_USE)
                .build();

        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(availableStation));
        when(chargingStationRepository.save(any(ChargingStation.class))).thenReturn(updatedStation);

        // When
        ChargingStation result = chargingStationService.updateStation(1L, updateDetails);

        // Then
        assertThat(result).isEqualTo(updatedStation);
        assertThat(result.getAddress()).isEqualTo("Calle Actualizada 123");
        assertThat(result.getChargerType()).isEqualTo(ChargerType.DC_FAST);
        verify(chargingStationRepository).findById(1L);
        verify(chargingStationRepository).save(any(ChargingStation.class));
        verify(cache).clear(); // Cache should be cleared after update
    }

    @Test
    void testUpdateStation_NotFound() {
        // Given
        ChargingStation updateDetails = ChargingStation.builder()
                .address("Calle Actualizada 123")
                .latitude(40.7140)
                .longitude(-74.0070)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(10)
                .status(StationStatus.IN_USE)
                .build();

        when(chargingStationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chargingStationService.updateStation(999L, updateDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Charging station not found with id: 999");

        verify(chargingStationRepository).findById(999L);
        verify(chargingStationRepository, never()).save(any());
        verify(cache, never()).clear();
    }

    @Test
    void testDeleteStation() {
        // Given
        when(chargingStationRepository.existsById(1L)).thenReturn(true);

        // When
        chargingStationService.deleteStation(1L);

        // Then
        verify(chargingStationRepository).deleteById(1L);
        verify(cache).clear(); // Cache should be cleared after deletion
    }

    @Test
    void testDeleteStation_NotFound() {
        // Given
        when(chargingStationRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> chargingStationService.deleteStation(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Charging station not found with id: 999");

        verify(chargingStationRepository).existsById(999L);
        verify(chargingStationRepository, never()).deleteById(any());
        verify(cache, never()).clear();
    }

    @Test
    void testGetStationsByChargerType() {
        // Given
        when(chargingStationRepository.findByChargerType(ChargerType.AC))
                .thenReturn(List.of(availableStation));

        // When
        List<ChargingStation> result = chargingStationService.getStationsByChargerType(ChargerType.AC);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChargerType()).isEqualTo(ChargerType.AC);
        verify(chargingStationRepository).findByChargerType(ChargerType.AC);
    }

    @Test
    void testGetStationsByStatus() {
        // Given
        when(chargingStationRepository.findByStatus(StationStatus.AVAILABLE))
                .thenReturn(List.of(availableStation));

        // When
        List<ChargingStation> result = chargingStationService.getStationsByStatus(StationStatus.AVAILABLE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StationStatus.AVAILABLE);
        verify(chargingStationRepository).findByStatus(StationStatus.AVAILABLE);
    }

    @Test
    void testGetAvailableStations_WithCache() {
        // Given
        when(cache.get("availableStations", List.class)).thenReturn(null);
        when(chargingStationRepository.findByStatus(StationStatus.AVAILABLE))
                .thenReturn(List.of(availableStation));

        // When
        List<ChargingStation> result = chargingStationService.getAvailableStations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StationStatus.AVAILABLE);
        verify(chargingStationRepository).findByStatus(StationStatus.AVAILABLE);
        verify(cache).put(eq("availableStations"), any(List.class));
    }

    @Test
    void testGetAvailableStations_FromCache() {
        // Given
        when(cache.get("availableStations", List.class)).thenReturn(List.of(availableStation));

        // When
        List<ChargingStation> result = chargingStationService.getAvailableStations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StationStatus.AVAILABLE);
        verify(chargingStationRepository, never()).findByStatus(any());
        verify(cache, never()).put(any(), any());
    }

    @Test
    void testGetInUseStations() {
        // Given
        when(chargingStationRepository.findByStatus(StationStatus.IN_USE))
                .thenReturn(List.of(inUseStation));

        // When
        List<ChargingStation> result = chargingStationService.getInUseStations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StationStatus.IN_USE);
        verify(chargingStationRepository).findByStatus(StationStatus.IN_USE);
    }

    @Test
    void testFindStationsByLocationRange() {
        // Given
        when(chargingStationRepository.findByLocationWithinRange(40.0, 41.0, -75.0, -74.0))
                .thenReturn(List.of(availableStation, inUseStation));

        // When
        List<ChargingStation> result = chargingStationService.findStationsByLocationRange(40.0, 41.0, -75.0, -74.0);

        // Then
        assertThat(result).hasSize(2);
        verify(chargingStationRepository).findByLocationWithinRange(40.0, 41.0, -75.0, -74.0);
    }

    @Test
    void testSearchStationsByAddress() {
        // Given
        when(chargingStationRepository.findByAddressContainingIgnoreCase("Principal"))
                .thenReturn(List.of(availableStation));

        // When
        List<ChargingStation> result = chargingStationService.searchStationsByAddress("Principal");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAddress()).contains("Principal");
        verify(chargingStationRepository).findByAddressContainingIgnoreCase("Principal");
    }

    @Test
    void testGetStationsWithMinChargingPoints() {
        // Given
        when(chargingStationRepository.findByChargingPointsGreaterThanEqual(6))
                .thenReturn(List.of(inUseStation));

        // When
        List<ChargingStation> result = chargingStationService.getStationsWithMinChargingPoints(6);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChargingPoints()).isGreaterThanOrEqualTo(6);
        verify(chargingStationRepository).findByChargingPointsGreaterThanEqual(6);
    }

    @Test
    void testChangeStationStatus() {
        // Given
        ChargingStation updatedStation = ChargingStation.builder()
                .id(1L)
                .address("Calle Principal 123")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.IN_USE)
                .build();

        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(availableStation));
        when(chargingStationRepository.save(any(ChargingStation.class))).thenReturn(updatedStation);

        // When
        ChargingStation result = chargingStationService.changeStationStatus(1L, StationStatus.IN_USE);

        // Then
        assertThat(result.getStatus()).isEqualTo(StationStatus.IN_USE);
        verify(chargingStationRepository).findById(1L);
        verify(chargingStationRepository).save(any(ChargingStation.class));
        verify(cache).clear(); // Cache should be cleared after status change
    }

    @Test
    void testGetStatistics() {
        // Given
        when(chargingStationRepository.countByStatus(StationStatus.AVAILABLE)).thenReturn(5L);
        when(chargingStationRepository.countByStatus(StationStatus.IN_USE)).thenReturn(3L);
        when(chargingStationRepository.count()).thenReturn(8L);

        // When
        ChargingStationService.StationStatistics result = chargingStationService.getStatistics();

        // Then
        assertThat(result.totalStations()).isEqualTo(8L);
        assertThat(result.availableStations()).isEqualTo(5L);
        assertThat(result.inUseStations()).isEqualTo(3L);
        verify(chargingStationRepository).countByStatus(StationStatus.AVAILABLE);
        verify(chargingStationRepository).countByStatus(StationStatus.IN_USE);
        verify(chargingStationRepository).count();
    }
}