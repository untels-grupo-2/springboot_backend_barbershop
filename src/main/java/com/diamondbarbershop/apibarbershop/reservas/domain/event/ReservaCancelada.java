package com.diamondbarbershop.apibarbershop.reservas.domain.event;

import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain Event: ocurre cuando una reserva es cancelada (por el cliente o el admin).
 *
 * Consumidores esperados:
 *   - NotificacionEmailReservaListener (PB-39) → email al cliente informando la cancelación.
 *
 * Se incluye fechaReserva para que el email pueda mostrar qué fecha se canceló,
 * y motivo para que el email sea informativo.
 */
public record ReservaCancelada(
        Long reservaId,
        Long clienteId,
        LocalDate fechaReserva,
        String motivo,
        LocalDateTime occurredOn
) implements DomainEvent {}
