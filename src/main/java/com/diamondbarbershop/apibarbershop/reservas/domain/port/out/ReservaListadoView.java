package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

import com.diamondbarbershop.apibarbershop.util.EstadoReserva;

import java.time.LocalDate;

/**
 * Read Model — proyección de solo lectura optimizada para listados de reservas (PB-14 + PB-20).
 *
 * No es el aggregate Reserva (que es para Commands: confirmar, cancelar, etc.).
 * Es un objeto pensado específicamente para mostrar listas en la UI:
 *   - Trae nombres ya resueltos (barbero, cliente, servicio) en una sola query JPA con JOINs
 *   - Evita el problema N+1 que tendría el dominio puro (que solo tiene IDs)
 *
 * Patrón: CQRS-lite — separar la consulta optimizada del modelo de dominio
 * sin armar dos modelos completos. El aggregate Reserva sigue siendo el
 * "Write Model"; esta View es el "Read Model".
 *
 * El adapter (ReservaJpaAdapter.buscarParaListado) construye estas instancias
 * desde ReservaEntity en un solo query con Specification + Pageable.
 * El application service traduce View → DtoReservaResponse para la respuesta HTTP.
 */
public record ReservaListadoView(
        Long reservaId,
        String barberoNombre,
        Long usuarioId,
        String usuarioNombre,
        String servicioNombre,
        String horarioRango,
        EstadoReserva estado,
        Long precio,
        LocalDate fechaReserva
) {}
