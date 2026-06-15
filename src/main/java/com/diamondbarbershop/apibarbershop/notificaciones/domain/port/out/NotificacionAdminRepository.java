package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de salida — persistencia del buzón de notificaciones del admin.
 */
public interface NotificacionAdminRepository {

    /**
     * Persiste una notificación nueva (siempre con leida = false).
     */
    NotificacionAdmin guardar(String titulo, String cuerpo, String tipo);

    /**
     * Lista paginada del buzón.
     *
     * @param soloNoLeidas si es true, filtra por leida = false; si es false o null,
     *                     trae todas
     */
    Page<NotificacionAdmin> listar(Boolean soloNoLeidas, Pageable pageable);

    /**
     * Marca como leída. No-op si la notificación no existe o ya estaba leída.
     */
    void marcarComoLeida(Long notificacionId);
}
