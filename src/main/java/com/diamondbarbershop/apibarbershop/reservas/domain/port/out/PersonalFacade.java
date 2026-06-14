package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

/**
 * Facade (GoF — Estructural) — PB-16.
 *
 * Puerto de SALIDA del BC Reservas hacia el BC Personal.
 *
 * Esconde detalles internos del BC Personal (entidad Barbero, repositorio,
 * lógica de gestión de personal) detrás de un contrato simple orientado a
 * lo que Reservas necesita preguntar.
 */
public interface PersonalFacade {

    /**
     * @return true si existe un barbero con ese ID
     */
    boolean existeBarbero(Long barberoId);

    /**
     * @return true si el barbero existe y está activo (puede atender reservas)
     */
    boolean estaActivoBarbero(Long barberoId);
}
