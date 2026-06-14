package com.diamondbarbershop.apibarbershop.reservas.application.strategy;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Selector — punto único de decisión para escoger qué MontoCalculoStrategy
 * aplicar a un CrearReservaCommand.
 *
 * Mantener la decisión aquí —y no en el Application Service— sigue el principio
 * de responsabilidad única: el service orquesta (validar → crear → guardar →
 * publicar), el selector decide.
 *
 * Si en el futuro hay nuevas estrategias (descuento, paquete, etc.), solo se
 * modifica este selector. Ni el Application Service ni las estrategias mismas
 * se enteran.
 *
 * Estado actual (PB-11):
 *   - Lee el flag command.usarRecompensa() y elige fidelidad o estándar.
 *
 * Estado futuro (PB-16):
 *   - Aquí se inyectará IdentidadFacade para validar que el cliente
 *     efectivamente tenga la recompensa disponible antes de aplicar fidelidad.
 *     Si la valida, aplica; si no, lanza excepción.
 */
@Component
@RequiredArgsConstructor
public class MontoCalculoStrategySelector {

    private final PrecioEstandarStrategy precioEstandarStrategy;
    private final PrecioFidelidadStrategy precioFidelidadStrategy;

    /**
     * Decide la estrategia aplicable según el contenido del command.
     */
    public MontoCalculoStrategy seleccionar(CrearReservaCommand command) {
        if (command.usarRecompensa()) {
            return precioFidelidadStrategy;
        }
        return precioEstandarStrategy;
    }
}
