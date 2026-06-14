package com.diamondbarbershop.apibarbershop.reservas.application.event;

import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publisher del patrón Observer (PB-13).
 *
 * Función: recibe la lista de eventos emitidos por un aggregate y los
 * distribuye a todos los listeners registrados.
 *
 * Cómo descubre listeners:
 *   Spring inyecta automáticamente la List<DomainEventListener> con todos
 *   los @Component que implementen la interfaz. No hay registro manual.
 *   Eso significa que agregar un listener nuevo es solo crear una clase
 *   con @Component implements DomainEventListener.
 *
 * Cómo decide quién recibe qué evento:
 *   El publisher NO decide. Llama onEvent() en TODOS los listeners y cada
 *   listener filtra internamente con pattern matching (instanceof). Eso
 *   mantiene al publisher agnóstico de los tipos concretos de evento.
 *
 * Sobre transacciones:
 *   El método publicar() corre dentro de la transacción del Application
 *   Service llamador (porque ese tiene @Transactional). Si un listener falla
 *   (lanza excepción), la transacción se hace rollback — la reserva nueva
 *   NO se persiste si algún efecto secundario crítico falla. Esto es lo
 *   deseado para mantener consistencia.
 */
@Component
@RequiredArgsConstructor
public class ReservaDomainEventPublisher {

    /**
     * Spring inyecta TODOS los listeners registrados como @Component.
     * Al arrancar la app, esta lista incluirá RecompensaListener (PB-13),
     * y en el futuro también NotificacionEmailClienteListener (PB-39) y
     * PushNotificacionAdminListener (PB-41).
     */
    private final List<DomainEventListener> listeners;

    /**
     * Distribuye una lista de eventos a todos los listeners registrados.
     * Mantiene el orden de los eventos: si un aggregate emitió
     * [ReservaCreada, ReservaConfirmada], se publican en ese orden.
     */
    public void publicar(List<DomainEvent> eventos) {
        for (DomainEvent evento : eventos) {
            for (DomainEventListener listener : listeners) {
                listener.onEvent(evento);
            }
        }
    }
}
