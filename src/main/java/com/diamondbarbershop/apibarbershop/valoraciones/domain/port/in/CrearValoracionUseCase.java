package com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in;

/**
 * Puerto de entrada — registrar una valoración del cliente sobre el servicio.
 */
public interface CrearValoracionUseCase {

    Long crear(CrearValoracionCommand command);

    record CrearValoracionCommand(
            Long clienteId,
            Integer puntuacion,    // 1 a 5
            Boolean util,
            String mensaje
    ) {}
}
