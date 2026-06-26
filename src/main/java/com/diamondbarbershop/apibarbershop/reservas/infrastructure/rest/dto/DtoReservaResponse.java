package com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DtoReservaResponse {
    private Long reservaId;
    private String barberoNombre;
    private String usuarioNombre;
    private Long usuarioId;
    private String horarioRango; // Puedes poner el rango como texto, ej: "09:00-10:00"
    private String estado;
    private String motivoDescripcion; // Lo pone el admin
    private String adicionales;       // Lo pone el usuario
    private LocalDateTime fechaCreacion;
    private LocalDate fechaReserva;
    private String servicioNombre;
    private Long precioServicio;
    private Integer estRecompensa;
    private String urlPago;
    private Long montoTotal;
}