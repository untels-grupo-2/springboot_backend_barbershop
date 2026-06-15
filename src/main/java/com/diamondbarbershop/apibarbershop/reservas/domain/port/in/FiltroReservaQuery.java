package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import com.diamondbarbershop.apibarbershop.util.EstadoReserva;

import java.time.LocalDate;

/**
 * Query Object con filtros opcionales para consultar reservas (PB-14).
 *
 * Cualquier campo nulo significa "no filtrar por ese criterio". El admin
 * puede combinar filtros libremente:
 *   - Sin filtros → todas las reservas paginadas
 *   - Solo barberoId → reservas de ese barbero
 *   - barberoId + estado + rango de fechas → combinación de los 3
 *
 * Las Specifications (en infrastructure/specification/ReservaSpecifications)
 * traducen este record en una consulta JPA compuesta dinámicamente.
 */
public record FiltroReservaQuery(
        Long barberoId,
        Long clienteId,
        EstadoReserva estado,
        LocalDate fechaDesde,
        LocalDate fechaHasta
) {}
