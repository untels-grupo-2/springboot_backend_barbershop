package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReporteResponse;

import java.time.LocalDate;

/**
 * Puerto de entrada — calcula el reporte de ganancias en un rango de fechas.
 *
 * Solo cuenta reservas en estado REALIZADA (las que de verdad generaron ingreso).
 */
public interface ObtenerReportesUseCase {

    DtoReporteResponse obtener(LocalDate fechaInicio, LocalDate fechaFin, String servicio);
}
