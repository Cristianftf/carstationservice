package com.station.carstationservice.repository;

import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChargingStationRepositoryTest {

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Test
    void testSaveAndFindById() {
        // Given
        ChargingStation station = ChargingStation.builder()
                .address("Calle Principal 123")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        // When
        ChargingStation savedStation = chargingStationRepository.save(station);
        Optional<ChargingStation> foundStation = chargingStationRepository.findById(savedStation.getId());

        // Then
        assertThat(foundStation).isPresent();
        assertThat(foundStation.get().getAddress()).isEqualTo("Calle Principal 123");
        assertThat(foundStation.get().getChargerType()).isEqualTo(ChargerType.AC);
    }

    @Test
    void testFindByChargerType() {
        // Given
        ChargingStation acStation = ChargingStation.builder()
                .address("AC Station")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation dcStation = ChargingStation.builder()
                .address("DC Station")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.AVAILABLE)
                .build();

        chargingStationRepository.save(acStation);
        chargingStationRepository.save(dcStation);

        // When
        List<ChargingStation> acStations = chargingStationRepository.findByChargerType(ChargerType.AC);
        List<ChargingStation> dcStations = chargingStationRepository.findByChargerType(ChargerType.DC_FAST);

        // Then
        assertThat(acStations).hasSize(1);
        assertThat(acStations.get(0).getChargerType()).isEqualTo(ChargerType.AC);
        assertThat(dcStations).hasSize(1);
        assertThat(dcStations.get(0).getChargerType()).isEqualTo(ChargerType.DC_FAST);
    }

    @Test
    void testFindByStatus() {
        // Given
        ChargingStation availableStation = ChargingStation.builder()
                .address("Available Station")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation inUseStation = ChargingStation.builder()
                .address("In Use Station")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.IN_USE)
                .build();

        chargingStationRepository.save(availableStation);
        chargingStationRepository.save(inUseStation);

        // When
        List<ChargingStation> availableStations = chargingStationRepository.findByStatus(StationStatus.AVAILABLE);
        List<ChargingStation> inUseStations = chargingStationRepository.findByStatus(StationStatus.IN_USE);

        // Then
        assertThat(availableStations).hasSize(1);
        assertThat(availableStations.get(0).getStatus()).isEqualTo(StationStatus.AVAILABLE);
        assertThat(inUseStations).hasSize(1);
        assertThat(inUseStations.get(0).getStatus()).isEqualTo(StationStatus.IN_USE);
    }

    @Test
    void testFindByChargingPointsGreaterThanEqual() {
        // Given
        ChargingStation station4Points = ChargingStation.builder()
                .address("4 Points Station")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation station8Points = ChargingStation.builder()
                .address("8 Points Station")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.AVAILABLE)
                .build();

        chargingStationRepository.save(station4Points);
        chargingStationRepository.save(station8Points);

        // When
        List<ChargingStation> stationsWithMin6Points = chargingStationRepository.findByChargingPointsGreaterThanEqual(6);
        List<ChargingStation> stationsWithMin4Points = chargingStationRepository.findByChargingPointsGreaterThanEqual(4);

        // Then
        assertThat(stationsWithMin6Points).hasSize(1);
        assertThat(stationsWithMin6Points.get(0).getChargingPoints()).isEqualTo(8);
        assertThat(stationsWithMin4Points).hasSize(2);
    }

    @Test
    void testExistsByAddress() {
        // Given
        ChargingStation station = ChargingStation.builder()
                .address("Calle Unica 456")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        chargingStationRepository.save(station);

        // When
        boolean exists = chargingStationRepository.existsByAddress("Calle Unica 456");
        boolean notExists = chargingStationRepository.existsByAddress("Calle Inexistente 999");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testCountByStatus() {
        // Given
        ChargingStation availableStation1 = ChargingStation.builder()
                .address("Available 1")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation availableStation2 = ChargingStation.builder()
                .address("Available 2")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation inUseStation = ChargingStation.builder()
                .address("In Use")
                .latitude(40.7130)
                .longitude(-74.0062)
                .chargerType(ChargerType.AC)
                .chargingPoints(6)
                .status(StationStatus.IN_USE)
                .build();

        chargingStationRepository.save(availableStation1);
        chargingStationRepository.save(availableStation2);
        chargingStationRepository.save(inUseStation);

        // When
        long availableCount = chargingStationRepository.countByStatus(StationStatus.AVAILABLE);
        long inUseCount = chargingStationRepository.countByStatus(StationStatus.IN_USE);

        // Then
        assertThat(availableCount).isEqualTo(2);
        assertThat(inUseCount).isEqualTo(1);
    }

    @Test
    void testFindByAddressContainingIgnoreCase() {
        // Given
        ChargingStation station1 = ChargingStation.builder()
                .address("Avenida Principal 123")
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(ChargerType.AC)
                .chargingPoints(4)
                .status(StationStatus.AVAILABLE)
                .build();

        ChargingStation station2 = ChargingStation.builder()
                .address("Calle Secundaria 456")
                .latitude(40.7129)
                .longitude(-74.0061)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(8)
                .status(StationStatus.AVAILABLE)
                .build();

        chargingStationRepository.save(station1);
        chargingStationRepository.save(station2);

        // When
        List<ChargingStation> principalStations = chargingStationRepository.findByAddressContainingIgnoreCase("principal");
        List<ChargingStation> secundariaStations = chargingStationRepository.findByAddressContainingIgnoreCase("SECUNDARIA");
        List<ChargingStation> notFoundStations = chargingStationRepository.findByAddressContainingIgnoreCase("inexistente");

        // Then
        assertThat(principalStations).hasSize(1);
        assertThat(principalStations.get(0).getAddress()).contains("Principal");
        assertThat(secundariaStations).hasSize(1);
        assertThat(secundariaStations.get(0).getAddress()).contains("Secundaria");
        assertThat(notFoundStations).isEmpty();
    }
}