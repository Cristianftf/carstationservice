package com.station.carstationservice.controller;

import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import com.station.carstationservice.service.ChargingStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charging-stations")
@RequiredArgsConstructor
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllStations() {
        return ResponseEntity.ok(chargingStationService.getAllStations());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ChargingStation>> getAllStationsPaged(Pageable pageable) {
        return ResponseEntity.ok(chargingStationService.getAllStations(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStation> getStationById(@PathVariable Long id) {
        return chargingStationService.getStationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChargingStation> createStation(@Valid @RequestBody ChargingStation station) {
        return ResponseEntity.ok(chargingStationService.createStation(station));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStation> updateStation(@PathVariable Long id, @Valid @RequestBody ChargingStation stationDetails) {
        return ResponseEntity.ok(chargingStationService.updateStation(id, stationDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        chargingStationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/charger-type/{chargerType}")
    public ResponseEntity<List<ChargingStation>> getStationsByChargerType(@PathVariable ChargerType chargerType) {
        return ResponseEntity.ok(chargingStationService.getStationsByChargerType(chargerType));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChargingStation>> getStationsByStatus(@PathVariable StationStatus status) {
        return ResponseEntity.ok(chargingStationService.getStationsByStatus(status));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ChargingStation>> getAvailableStations() {
        return ResponseEntity.ok(chargingStationService.getAvailableStations());
    }

    @GetMapping("/in-use")
    public ResponseEntity<List<ChargingStation>> getInUseStations() {
        return ResponseEntity.ok(chargingStationService.getInUseStations());
    }

    @GetMapping("/location-range")
    public ResponseEntity<List<ChargingStation>> findStationsByLocationRange(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        return ResponseEntity.ok(chargingStationService.findStationsByLocationRange(minLat, maxLat, minLon, maxLon));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChargingStation>> searchStationsByAddress(@RequestParam String address) {
        return ResponseEntity.ok(chargingStationService.searchStationsByAddress(address));
    }

    @GetMapping("/min-points/{minPoints}")
    public ResponseEntity<List<ChargingStation>> getStationsWithMinChargingPoints(@PathVariable Integer minPoints) {
        return ResponseEntity.ok(chargingStationService.getStationsWithMinChargingPoints(minPoints));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ChargingStation> changeStationStatus(@PathVariable Long id, @RequestParam StationStatus status) {
        return ResponseEntity.ok(chargingStationService.changeStationStatus(id, status));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ChargingStationService.StationStatistics> getStatistics() {
        return ResponseEntity.ok(chargingStationService.getStatistics());
    }
}