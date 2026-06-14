package com.diamondbarbershop.apibarbershop.reservas.application.strategy;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;

/**
 * Strategy (GoF — Comportamiento) — PB-11.
 *
 * Contrato común para calcular el monto final de una reserva.
 * Distintas implementaciones aplican reglas de negocio distintas:
 *   - PrecioEstandarStrategy   → cobra el precio del servicio sin modificación
 *   - PrecioFidelidadStrategy  → cobra 0 (reserva gratuita por recompensa)
 *
 * En el futuro se podrán agregar más estrategias sin tocar el código consumidor:
 *   - PrecioDescuentoStrategy  → aplicar promoción activa o código de descuento
 *   - PrecioPaqueteStrategy    → tarifa especial por reserva de paquete múltiple
 *
 * El componente que decide cuál estrategia usar es MontoCalculoStrategySelector.
 * El que la consume es CrearReservaApplicationService.
 */
public interface MontoCalculoStrategy {

    /**
     * Calcula el monto final a cobrar por la reserva.
     *
     * @param command datos de la reserva en construcción
     * @return monto final en la moneda del sistema (Long, sin decimales)
     */
    Long calcular(CrearReservaCommand command);
}
