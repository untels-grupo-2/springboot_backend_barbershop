package com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out;

/**
 * Read Model para listados administrativos de valoraciones.
 *
 * Trae nombre y celular del cliente ya resueltos (via JOIN en el adapter),
 * evitando consultas adicionales en el application service.
 */
public record ValoracionListadoView(
        Long valoracionId,
        Long clienteId,
        String clienteNombre,
        String clienteCelular,
        Integer puntuacion,
        Boolean util,
        String mensaje,
        Integer estado
) {}
