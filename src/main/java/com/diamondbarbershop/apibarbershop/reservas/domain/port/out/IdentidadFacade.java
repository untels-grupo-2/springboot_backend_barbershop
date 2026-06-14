package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

/**
 * Facade (GoF — Estructural) — PB-16.
 *
 * Puerto de SALIDA del BC Reservas hacia el BC Identidad.
 *
 * Esconde detalles internos del BC Identidad (entidad Usuario, JWT, roles,
 * refresh tokens, password hashes) detrás de un contrato simple orientado
 * a lo que Reservas necesita: confirmar que el cliente es válido.
 *
 * NOTA sobre recompensa: la validación "¿el cliente tiene recompensa
 * acumulada?" NO va en esta Facade porque la información está en el propio
 * BC Reservas (campo estRecompensa en las reservas del cliente). Eso es
 * lógica interna del BC Reservas, no del BC Identidad.
 */
public interface IdentidadFacade {

    /**
     * @return true si existe un usuario con ese ID
     */
    boolean existeUsuario(Long usuarioId);

    /**
     * @return true si el usuario existe y su cuenta está activa
     */
    boolean estaActivoUsuario(Long usuarioId);
}
