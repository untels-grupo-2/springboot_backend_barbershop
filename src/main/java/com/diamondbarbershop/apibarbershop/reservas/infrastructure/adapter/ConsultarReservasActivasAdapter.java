package com.diamondbarbershop.apibarbershop.reservas.infrastructure.adapter;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.ConsultarReservasActivasPort;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioRangoJpaEntity;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.ReservaJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioRangoJpaRepository;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.persistance.IReservaJpaRepository;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador cross-BC — el BC Reservas implementa el puerto de salida
 * `ConsultarReservasActivasPort` declarado por el BC Agenda.
 *
 * "Activa" significa una reserva que NO ha sido cancelada (estados
 * CREADA / CONFIRMADA / REALIZADA) — son las que ocupan efectivamente un slot.
 *
 * Anti-Corruption Layer: traduce conceptos del BC Reservas (estado, fecha,
 * rango) en un boolean simple que es lo único que necesita el BC Agenda.
 */
@Component
@RequiredArgsConstructor
public class ConsultarReservasActivasAdapter implements ConsultarReservasActivasPort {

    private final IReservaJpaRepository reservaJpaRepository;
    private final IHorarioRangoJpaRepository horarioRangoJpaRepository;

    @Override
    public boolean existeReservaActiva(Long barberoId, LocalDate fecha, Long horarioRangoId) {
        Optional<HorarioRangoJpaEntity> rangoOpt = horarioRangoJpaRepository.findById(horarioRangoId);
        if (rangoOpt.isEmpty()) {
            return false;
        }

        List<ReservaJpaEntity> reservas =
                reservaJpaRepository.findByFechaReservaAndHorarioRango(fecha, rangoOpt.get());

        return reservas.stream()
                .filter(r -> r.getBarbero().getBarbero_id().equals(barberoId))
                .anyMatch(r -> r.getEstado() != EstadoReserva.CANCELADA);
    }
}
