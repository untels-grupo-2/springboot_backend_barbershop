package com.diamondbarbershop.apibarbershop.reservas.domain.event;

import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain Event: ocurre cuando un cliente crea una nueva reserva.
 *
 * Consumidores esperados:
 *   - RecompensaListener (PB-13)   → si usaRecompensa = true, consume las reservas
 *                                    anteriores del cliente con estRecompensa = 0.
 *   - PushNotificacionListener (PB-41) → notifica al admin de nueva reserva.
 *
 * Usamos Java Record porque los eventos son inmutables por naturaleza:
 * lo que pasó no se puede cambiar, por lo tanto sus datos tampoco.
 *
 * Campo usaRecompensa (PB-13):
 *   Indica si esta reserva se creó consumiendo la recompensa de fidelidad
 *   acumulada del cliente. Permite a los listeners diferenciar reservas
 *   normales de reservas-recompensa sin tener que volver a consultar la BD.
 */
public record ReservaCreada(
        Long reservaId,
        Long barberoId,
        Long clienteId,
        Long servicioId,
        LocalDate fechaReserva,
        Long horarioRangoId,
        boolean usaRecompensa,
        LocalDateTime occurredOn
) implements DomainEvent {}
