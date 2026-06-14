package com.diamondbarbershop.apibarbershop.reservas.application.strategy;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import org.springframework.stereotype.Component;

/**
 * Strategy concreta: cobra el precio del servicio sin modificación.
 *
 * Se aplica al flujo "normal" de creación de reserva (cliente que no usa
 * recompensa ni promociones).
 *
 * Es @Component porque el Selector la inyecta directamente.
 */
@Component
public class PrecioEstandarStrategy implements MontoCalculoStrategy {

    @Override
    public Long calcular(CrearReservaCommand command) {
        return command.precioServicio();
    }
}
