package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;

/**
 * Handler #2 de la cadena: valida que el slot solicitado (barbero + fecha + rango)
 * no esté ya ocupado por otra reserva activa (no cancelada).
 *
 * Esta validación reemplaza el bloque que vivía inline en
 * CrearReservaApplicationService antes del refactor de PB-15.
 *
 * No es @Component porque Spring lo construye vía CadenaValidacionReservaConfig,
 * que le inyecta el ReservaRepository por constructor.
 */
public class ValidarHorarioDisponibleHandler extends ValidacionReservaHandler {

    private final ReservaRepository reservaRepository;

    public ValidarHorarioDisponibleHandler(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    protected void doValidar(CrearReservaCommand command) {
        boolean espacioOcupado = reservaRepository
                .findByBarberoIdAndFecha(command.barberoId(), command.fechaReserva())
                .stream()
                .anyMatch(reserva ->
                        reserva.getHorarioRangoId().equals(command.horarioRangoId())
                                && !reserva.estaCancelada()
                );

        if (espacioOcupado) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El barbero ya tiene una reserva activa en ese horario"
            );
        }
    }
}
