package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

import com.diamondbarbershop.apibarbershop.util.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Read Model — proyección de solo lectura para listados de reservas (PB-14 + PB-20).
 *
 * Hibernate ya carga la entidad completa, así que proyectamos todos los campos
 * que el frontend necesita para evitar nulls innecesarios en la respuesta.
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
        LocalDate fechaReserva,
        String motivoDescripcion,
        String adicionales,
        LocalDateTime fechaCreacion,
        Integer estRecompensa,
        String urlPago
) {}
