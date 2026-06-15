package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada — listar las notificaciones que el admin tiene en su buzón.
 *
 * Paginado para que la app móvil cargue por lotes (no toda la historia de golpe).
 * Por defecto el orden es por fecha descendente — las más recientes primero.
 */
public interface ListarNotificacionesAdminUseCase {

    Page<NotificacionAdmin> listar(Boolean soloNoLeidas, Pageable pageable);
}
