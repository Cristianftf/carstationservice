package com.station.carstationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "charging_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String address;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de cargador es obligatorio")
    @Column(name = "charger_type", nullable = false)
    private ChargerType chargerType;

    @Min(value = 1, message = "Debe tener al menos 1 punto de carga")
    @Max(value = 20, message = "No puede tener más de 20 puntos de carga")
    @Column(name = "charging_points", nullable = false)
    private Integer chargingPoints;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private StationStatus status;

    public enum ChargerType {
        AC, DC_FAST
    }

    public enum StationStatus {
        AVAILABLE, IN_USE
    }
}