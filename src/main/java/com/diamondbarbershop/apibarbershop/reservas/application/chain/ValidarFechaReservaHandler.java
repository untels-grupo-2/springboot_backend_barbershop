package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;

import java.time.LocalDate;

/**
 * Handler #1 de la cadena: valida que la fecha solicitada para la reserva
 * no sea anterior al día de hoy.
 *
 * Nota sobre defensa en profundidad:
 *   El agregado Reserva.crear() también valida esta misma regla.
 *   Mantener ambas validaciones es intencional:
 *     - La cadena valida ANTES (orquestación) → falla rápido con mensaje claro.
 *     - El agregado valida DESPUÉS (invariante de dominio) → protege la
 *       consistencia incluso si alguien llamara al agregado desde otro lugar.
 *
 * No es @Component porque Spring lo construye vía CadenaValidacionReservaConfig.
 */
public class ValidarFechaReservaHandler extends ValidacionReservaHandler {

    @Override
    protected void doValidar(CrearReservaCommand command) {
        if (command.fechaReserva().isBefore(LocalDate.now())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "No se puede crear una reserva en una fecha pasada. Fecha recibida: "
                            + command.fechaReserva()
            );
        }
    }
}
