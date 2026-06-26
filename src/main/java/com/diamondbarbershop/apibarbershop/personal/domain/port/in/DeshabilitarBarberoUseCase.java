package com.diamondbarbershop.apibarbershop.personal.domain.port.in;

/**
 * Puerto de entrada — baja lógica de un barbero (no eliminación).
 * Las reservas pasadas que lo referenciaban se mantienen intactas.
 */
public interface DeshabilitarBarberoUseCase {

    void deshabilitar(Long barberoId);
}
