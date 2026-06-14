package com.diamondbarbershop.apibarbershop.shared.domain.event;

/**
 * Observer (GoF — Comportamiento) — PB-13.
 *
 * Contrato común que deben implementar todas las clases interesadas en
 * reaccionar a Domain Events.
 *
 * Convención de uso:
 *   - Cada @Component que implemente DomainEventListener se registra
 *     automáticamente con el ReservaDomainEventPublisher (Spring inyecta
 *     List<DomainEventListener>).
 *   - El listener decide internamente qué eventos le importan, mediante
 *     pattern matching:
 *
 *       @Override
 *       public void onEvent(DomainEvent event) {
 *           if (event instanceof ReservaCreada e) {
 *               // reaccionar
 *           }
 *       }
 *
 * Beneficio:
 *   Agregar una nueva reacción a eventos = nueva clase @Component, sin tocar
 *   ningún otro código. Eso es exactamente lo que prometía el patrón Observer.
 */
public interface DomainEventListener {

    void onEvent(DomainEvent event);
}
