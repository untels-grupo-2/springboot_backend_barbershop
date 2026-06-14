package com.diamondbarbershop.apibarbershop.reservas.application.strategy;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import org.springframework.stereotype.Component;

/**
 * Strategy concreta: cobra 0 — la reserva es gratuita por recompensa de fidelidad.
 *
 * Se aplica cuando el cliente declara explícitamente que quiere consumir su
 * recompensa acumulada (command.usarRecompensa() == true).
 *
 * Esta estrategia REEMPLAZA el bug del legacy ReservaService.crearReservaRecompensa(),
 * donde el cliente "consumía" sus 7 reservas acumuladas pero igual pagaba la 8va
 * al precio normal del servicio. Con Strategy aplicada correctamente, el monto
 * queda en 0 y la reserva tampoco suma a las ganancias del dashboard.
 *
 * Es @Component porque el Selector la inyecta directamente.
 *
 * NOTA: la validación de que el cliente EFECTIVAMENTE tiene la recompensa
 * disponible se incorpora en PB-16 (Facade) con IdentidadFacade. Hoy el
 * backend confía en el flag enviado por el cliente.
 */
@Component
public class PrecioFidelidadStrategy implements MontoCalculoStrategy {

    @Override
    public Long calcular(CrearReservaCommand command) {
        return 0L;
    }
}
