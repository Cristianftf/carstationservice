
package com.station.carstationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.station.carstationservice.model.ChargingStation;
import com.station.carstationservice.model.ChargingStation.ChargerType;
import com.station.carstationservice.model.ChargingStation.StationStatus;
import com.station.carstationservice.service.ChargingStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChargingStationController.class)
class ChargingStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ChargingStationService chargingStationService;

    private ChargingStation createTestStation(Long id, String address, ChargerType chargerType, StationStatus status) {
        return ChargingStation.builder()
                .id(id)
                .address(address)
                .latitude(40.7128)
                .longitude(-74.0060)
                .chargerType(chargerType)
                .chargingPoints(4)
                .status(status)
                .build();
    }

    @Test
    void testGetAllStations() throws Exception {
        // Given
        ChargingStation station1 = createTestStation(1L, "Calle Principal 123", ChargerType.AC, StationStatus.AVAILABLE);
        ChargingStation station2 = createTestStation(2L, "Avenida Secundaria 456", ChargerType.DC_FAST, StationStatus.IN_USE);
        
        when(chargingStationService.getAllStations()).thenReturn(List.of(station1, station2));

        // When & Then
        mockMvc.perform(get("/api/charging-stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("Calle Principal 123"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].address").value("Avenida Secundaria 456"));

        verify(chargingStationService).getAllStations();
    }

    @Test
    void testGetAllStationsPaged() throws Exception {
        // Given
        ChargingStation station1 = createTestStation(1L, "Calle Principal 123", ChargerType.AC, StationStatus.AVAILABLE);
        Page<ChargingStation> page = new PageImpl<>(List.of(station1), PageRequest.of(0, 10), 1);
        
        when(chargingStationService.getAllStations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/charging-stations/paged")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].address").value("Calle Principal 123"));

        verify(chargingStationService).getAllStations(any());
    }

    @Test
    void testGetStationById_Found() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "Calle Principal 123", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.getStationById(1L)).thenReturn(Optional.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Calle Principal 123"))
                .andExpect(jsonPath("$.chargerType").value("AC"));

        verify(chargingStationService).getStationById(1L);
    }

    @Test
    void testGetStationById_NotFound() throws Exception {
        // Given
        when(chargingStationService.getStationById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/charging-stations/999"))
                .andExpect(status().isNotFound());

        verify(chargingStationService).getStationById(999L);
    }

    @Test
    void testCreateStation() throws Exception {
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

        when(chargingStationService.createStation(any())).thenReturn(savedStation);

        // When & Then
        mockMvc.perform(post("/api/charging-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.address").value("Nueva Calle 789"));

        verify(chargingStationService).createStation(any());
    }

    @Test
    void testUpdateStation() throws Exception {
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

        when(chargingStationService.updateStation(eq(1L), any())).thenReturn(updatedStation);

        // When & Then
        mockMvc.perform(put("/api/charging-stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Calle Actualizada 123"))
                .andExpect(jsonPath("$.chargerType").value("DC_FAST"));

        verify(chargingStationService).updateStation(eq(1L), any());
    }

    @Test
    void testDeleteStation() throws Exception {
        // Given
        doNothing().when(chargingStationService).deleteStation(1L);

        // When & Then
        mockMvc.perform(delete("/api/charging-stations/1"))
                .andExpect(status().isNoContent());

        verify(chargingStationService).deleteStation(1L);
    }

    @Test
    void testGetStationsByChargerType() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "AC Station", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.getStationsByChargerType(ChargerType.AC)).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/charger-type/AC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].chargerType").value("AC"));

        verify(chargingStationService).getStationsByChargerType(ChargerType.AC);
    }

    @Test
    void testGetStationsByStatus() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "Available Station", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.getStationsByStatus(StationStatus.AVAILABLE)).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));

        verify(chargingStationService).getStationsByStatus(StationStatus.AVAILABLE);
    }

    @Test
    void testGetAvailableStations() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "Available Station", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.getAvailableStations()).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));

        verify(chargingStationService).getAvailableStations();
    }

    @Test
    void testGetInUseStations() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "In Use Station", ChargerType.DC_FAST, StationStatus.IN_USE);
        when(chargingStationService.getInUseStations()).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/in-use"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("IN_USE"));

        verify(chargingStationService).getInUseStations();
    }

    @Test
    void testFindStationsByLocationRange() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "Range Station", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.findStationsByLocationRange(40.0, 41.0, -75.0, -74.0)).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/location-range")
                .param("minLat", "40.0")
                .param("maxLat", "41.0")
                .param("minLon", "-75.0")
                .param("maxLon", "-74.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].address").value("Range Station"));

        verify(chargingStationService).findStationsByLocationRange(40.0, 41.0, -75.0, -74.0);
    }

    @Test
    void testSearchStationsByAddress() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "Main Street Station", ChargerType.AC, StationStatus.AVAILABLE);
        when(chargingStationService.searchStationsByAddress("Main")).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/search")
                .param("address", "Main"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].address").value("Main Street Station"));

        verify(chargingStationService).searchStationsByAddress("Main");
    }

    @Test
    void testGetStationsWithMinChargingPoints() throws Exception {
        // Given
        ChargingStation station = createTestStation(1L, "High Capacity Station", ChargerType.DC_FAST, StationStatus.AVAILABLE);
        when(chargingStationService.getStationsWithMinChargingPoints(8)).thenReturn(List.of(station));

        // When & Then
        mockMvc.perform(get("/api/charging-stations/min-points/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].chargingPoints").value(4)); // El test station tiene 4 puntos

        verify(chargingStationService).getStationsWithMinChargingPoints(8);
    }

    @Test
    void testChangeStationStatus() throws Exception {
        // Given
        ChargingStation updatedStation = createTestStation(1L, "Updated Status Station", ChargerType.AC, StationStatus.IN_USE);
        when(chargingStationService.changeStationStatus(1L, StationStatus.IN_USE)).thenReturn(updatedStation);

        // When & Then
        mockMvc.perform(patch("/api/charging-stations/1/status")
                .param("status", "IN_USE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_USE"));

        verify(chargingStationService).changeStationStatus(1L, StationStatus.IN_USE);
    }

    @Test
    void testGetStatistics() throws Exception {
        // Given
        ChargingStationService.StationStatistics statistics =
            new ChargingStationService.StationStatistics(10L, 7L, 3L);
        when(chargingStationService.getStatistics()).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/api/charging-stations/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStations").value(10))
                .andExpect(jsonPath("$.availableStations").value(7))
                .andExpect(jsonPath("$.inUseStations").value(3));

        verify(chargingStationService).getStatistics();
    }

    @Test
    void testCreateStation_ValidationError() throws Exception {
        // Given - station sin dirección requerida
        ChargingStation invalidStation = ChargingStation.builder()
                .latitude(40.7130)
                .longitude(-74.0062)
                .chargerType(ChargerType.AC)
                .chargingPoints(6)
                .status(StationStatus.AVAILABLE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/charging-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStation)))
                .andExpect(status().isBadRequest());

        verify(chargingStationService, never()).createStation(any());
    }

    @Test
    void testUpdateStation_NotFound() throws Exception {
        // Given
        ChargingStation updateDetails = ChargingStation.builder()
                .address("Calle Actualizada 123")
                .latitude(40.7140)
                .longitude(-74.0070)
                .chargerType(ChargerType.DC_FAST)
                .chargingPoints(10)
                .status(StationStatus.IN_USE)
                .build();

        when(chargingStationService.updateStation(eq(999L), any())).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/charging-stations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());

        verify(chargingStationService).updateStation(eq(999L), any());
    }
}