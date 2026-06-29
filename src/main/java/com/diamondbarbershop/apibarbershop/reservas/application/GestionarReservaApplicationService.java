package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.application.event.ReservaDomainEventPublisher;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.GestionarReservaUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso: Gestionar estados de una Reserva.
 *
 * Patrón: el Application Service carga el aggregate, delega la lógica
 * al dominio y persiste el resultado.
 * Nunca pone lógica de negocio aquí — eso es responsabilidad del aggregate.
 *
 * Después del refactor de PB-13 (Observer), publica los Domain Events
 * emitidos por el aggregate a través de ReservaDomainEventPublisher.
 * Los listeners reaccionan automáticamente:
 *   - (Futuro) NotificacionEmailClienteListener (PB-39) → email al cliente
 *     cuando confirmar/cancelar emiten sus eventos.
 */
@Service
@RequiredArgsConstructor
public class GestionarReservaApplicationService implements GestionarReservaUseCase {

    private final ReservaRepository reservaRepository;
    private final ReservaDomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public void confirmar(Long reservaId) {
        Reserva reserva = cargar(reservaId);

        // Dominio valida la transición: solo CREADA → CONFIRMADA
        reserva.confirmar();

        List<DomainEvent> eventos = reserva.pullEvents();
        reservaRepository.save(reserva);
        eventPublisher.publicar(eventos);
    }

    @Override
    @Transactional
    public void marcarComoRealizada(Long reservaId) {
        Reserva reserva = cargar(reservaId);

        // Dominio valida la transición: solo CONFIRMADA → REALIZADA
        reserva.marcarComoRealizada();

        List<DomainEvent> eventos = reserva.pullEvents();
        reservaRepository.save(reserva);
        eventPublisher.publicar(eventos);
    }

    @Override
    @Transactional
    public void cancelar(Long reservaId, String motivo) {
        Reserva reserva = cargar(reservaId);

        // Dominio valida: no se puede cancelar una REALIZADA
        reserva.cancelar(motivo);

        List<DomainEvent> eventos = reserva.pullEvents();
        reservaRepository.save(reserva);
        eventPublisher.publicar(eventos);
    }

    // ── Helpers privados ─────────────────────────────────────────────────────────

    private Reserva cargar(Long reservaId) {
        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + reservaId));
    }
}
