package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;

/**
 * Handler de la cadena: valida que el cliente que está creando la reserva
 * exista y su cuenta esté activa.
 *
 * Consume el BC Identidad vía IdentidadFacade (PB-16). No conoce nada de
 * la estructura interna de Identidad (Usuario, roles, tokens, etc.).
 *
 * No es @Component porque se construye en CadenaValidacionReservaConfig.
 */
public class ValidarUsuarioActivoHandler extends ValidacionReservaHandler {

    private final IdentidadFacade identidadFacade;

    public ValidarUsuarioActivoHandler(IdentidadFacade identidadFacade) {
        this.identidadFacade = identidadFacade;
    }

    @Override
    protected void doValidar(CrearReservaCommand command) {
        if (!identidadFacade.existeUsuario(command.clienteId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El cliente con ID " + command.clienteId() + " no existe"
            );
        }
        if (!identidadFacade.estaActivoUsuario(command.clienteId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "La cuenta del cliente con ID " + command.clienteId() + " no está activa"
            );
        }
    }
}
