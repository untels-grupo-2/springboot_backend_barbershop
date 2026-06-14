package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.PersonalFacade;

/**
 * Handler de la cadena: valida que el barbero referenciado por el command
 * exista y esté activo.
 *
 * Consume el BC Personal vía PersonalFacade (PB-16). No conoce nada de la
 * estructura interna de Personal (entidades, repositorios, etc.) — solo
 * pregunta lo que necesita.
 *
 * No es @Component porque se construye en CadenaValidacionReservaConfig.
 */
public class ValidarBarberoExisteHandler extends ValidacionReservaHandler {

    private final PersonalFacade personalFacade;

    public ValidarBarberoExisteHandler(PersonalFacade personalFacade) {
        this.personalFacade = personalFacade;
    }

    @Override
    protected void doValidar(CrearReservaCommand command) {
        if (!personalFacade.existeBarbero(command.barberoId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El barbero con ID " + command.barberoId() + " no existe"
            );
        }
        if (!personalFacade.estaActivoBarbero(command.barberoId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El barbero con ID " + command.barberoId() + " no está activo"
            );
        }
    }
}
