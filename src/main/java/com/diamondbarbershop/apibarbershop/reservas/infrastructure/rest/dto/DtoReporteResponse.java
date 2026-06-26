package com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto;

import lombok.Data;

@Data
public class DtoReporteResponse {

    private String servicioNombre;
    private Long montoTotal;
    private Integer cantidadReservas;
}
