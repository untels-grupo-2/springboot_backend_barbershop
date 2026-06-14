package com.diamondbarbershop.apibarbershop.reservas.application.listeners;

import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
import com.diamondbarbershop.apibarbershop.reservas.domain.model.Reserva;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener (Observer concreto) — PB-13.
 *
 * Reacciona a ReservaCreada con usaRecompensa = true: marca como consumidas
 * las reservas anteriores del cliente que estaban pendientes (estRecompensa = 0).
 *
 * Resuelve el TODO que dejamos en PB-11 cuando implementamos PrecioFidelidadStrategy:
 * la strategy se encargaba de poner el precio en 0, pero alguien tenía que
 * "consumir" el crédito acumulado del cliente. Ese alguien es este listener.
 *
 * Por qué un listener y no lógica en el Application Service:
 *   - El Application Service se mantiene como orquestador puro.
 *   - Agregar más efectos secundarios (email, push, historial) no implica
 *     modificarlo — solo crear más listeners.
 *   - Si mañana cambia la lógica de recompensa (consumir solo 7, no todas),
 *     se modifica únicamente este archivo.
 *
 * Sobre cuántas reservas se consumen:
 *   La implementación actual replica el comportamiento del legacy:
 *   marca TODAS las reservas con estRecompensa = 0 del cliente.
 *
 *   Si el negocio quiere consumir EXACTAMENTE 7 (las más antiguas, dejando
 *   las demás disponibles para una próxima recompensa), basta con cambiar
 *   el filter+forEach por un sorted().limit(7).
 *
 * Sobre transaccionalidad:
 *   Este listener corre dentro de la transacción del CrearReservaApplicationService.
 *   Si el save() de alguna de las reservas falla, hace rollback — la nueva
 *   reserva con precio 0 tampoco queda persistida. Es la consistencia correcta.
 */
@Component
@RequiredArgsConstructor
public class RecompensaListener implements DomainEventListener {

    private final ReservaRepository reservaRepository;

    @Override
    public void onEvent(DomainEvent event) {
        // Filtro por tipo de evento: solo reacciono a ReservaCreada con recompensa.
        if (!(event instanceof ReservaCreada e)) {
            return;
        }
        if (!e.usaRecompensa()) {
            return;
        }

        // Carga todas las reservas del cliente y filtra las pendientes de recompensa
        // (estRecompensa == 0). Excluimos la reserva recién creada (que ya tiene
        // estRecompensa == 1 desde Reserva.crear(), pero por seguridad la excluimos
        // explícitamente por ID).
        List<Reserva> pendientesDeRecompensa = reservaRepository
                .findByClienteId(e.clienteId())
                .stream()
                .filter(r -> r.getEstRecompensa() != null && r.getEstRecompensa() == 0)
                .filter(r -> !r.getId().equals(e.reservaId()))
                .toList();

        // Marcar cada una como consumida y persistir.
        for (Reserva pendiente : pendientesDeRecompensa) {
            pendiente.consumirParaRecompensa();
            reservaRepository.save(pendiente);
        }
    }
}
