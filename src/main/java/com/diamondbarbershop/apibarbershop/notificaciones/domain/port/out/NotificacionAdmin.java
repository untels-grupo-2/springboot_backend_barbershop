package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out;

import java.time.LocalDateTime;

/**
 * Modelo de notificación que el admin recibe.
 * Es un record inmutable — representa una notificación tal como vive en
 * la BD.
 *
 * Vive en `domain/port/out` porque es el formato que el repositorio expone
 * hacia el dominio (no se expone JpaEntity al application layer).
 */
public record NotificacionAdmin(
        Long id,
        String titulo,
        String cuerpo,
        String tipo,
        boolean leida,
        LocalDateTime createdAt
) {}
