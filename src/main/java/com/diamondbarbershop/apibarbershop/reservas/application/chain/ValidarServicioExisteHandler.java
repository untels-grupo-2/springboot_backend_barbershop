package com.diamondbarbershop.apibarbershop.reservas.application.chain;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.CrearReservaUseCase.CrearReservaCommand;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.CatalogoFacade;

/**
 * Handler de la cadena: valida que el servicio referenciado por el command
 * exista y esté activo en el catálogo.
 *
 * Consume el BC Catálogo vía CatalogoFacade (PB-16). No conoce nada de la
 * estructura interna de Catálogo (ServicioEntity, repositorios, etc.).
 *
 * No es @Component porque se construye en CadenaValidacionReservaConfig.
 */
public class ValidarServicioExisteHandler extends ValidacionReservaHandler {

    private final CatalogoFacade catalogoFacade;

    public ValidarServicioExisteHandler(CatalogoFacade catalogoFacade) {
        this.catalogoFacade = catalogoFacade;
    }

    @Override
    protected void doValidar(CrearReservaCommand command) {
        if (!catalogoFacade.existeServicio(command.servicioId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El servicio con ID " + command.servicioId() + " no existe"
            );
        }
        if (!catalogoFacade.estaActivoServicio(command.servicioId())) {
            throw new ReservaValidacionException(
                    this.getClass().getSimpleName(),
                    "El servicio con ID " + command.servicioId() + " no está disponible"
            );
        }
    }
}
