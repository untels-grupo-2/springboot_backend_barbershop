package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in;

/**
 * Puerto de entrada — marcar una notificación como leída.
 * La app móvil llama a este caso de uso cuando el admin abre la notificación.
 */
public interface MarcarNotificacionLeidaUseCase {

    void marcar(Long notificacionId);
}
