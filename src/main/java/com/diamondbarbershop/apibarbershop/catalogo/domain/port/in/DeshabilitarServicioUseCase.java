package com.diamondbarbershop.apibarbershop.catalogo.domain.port.in;

/**
 * Puerto de entrada — baja lógica de un servicio (estado = 0).
 *
 * NO elimina de la base. El servicio mantiene su historial en las reservas
 * que ya lo referenciaron. Solo se oculta de los listados de selección.
 */
public interface DeshabilitarServicioUseCase {

    void deshabilitar(Long servicioId);
}
