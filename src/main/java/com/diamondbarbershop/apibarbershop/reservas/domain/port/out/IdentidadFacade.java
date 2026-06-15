package com.diamondbarbershop.apibarbershop.reservas.domain.port.out;

import java.util.Optional;

/**
 * Facade (GoF — Estructural) — PB-16, extendida en PB-39.
 *
 * Puerto de SALIDA del BC Reservas hacia el BC Identidad.
 *
 * Esconde detalles internos del BC Identidad (entidad Usuario, JWT, roles,
 * refresh tokens, password hashes) detrás de un contrato simple orientado
 * a lo que Reservas necesita: confirmar que el cliente es válido y obtener
 * sus datos de contacto para notificaciones.
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

    /**
     * Devuelve el email del usuario, si existe.
     * Usado por NotificacionEmailReservaListener (PB-39) para enviar correos
     * de confirmación / cancelación al cliente.
     *
     * @return Optional con el email, o vacío si el usuario no existe
     */
    Optional<String> obtenerEmail(Long usuarioId);

    /**
     * Devuelve el nombre del usuario, si existe.
     * Usado para personalizar saludos en los correos de notificación.
     *
     * @return Optional con el nombre, o vacío si el usuario no existe
     */
    Optional<String> obtenerNombre(Long usuarioId);
}
