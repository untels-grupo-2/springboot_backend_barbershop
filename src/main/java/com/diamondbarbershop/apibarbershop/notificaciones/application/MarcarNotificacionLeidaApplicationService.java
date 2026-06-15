package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.MarcarNotificacionLeidaUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para marcar una notificación como leída (PB-41).
 *
 * Idempotente: si la notificación no existe o ya estaba leída, no hace nada.
 * No lanza error porque el caso "ya leída" es un estado válido y la app
 * móvil no debería preocuparse por races condition (dos clicks rápidos, etc.).
 */
@Service
@RequiredArgsConstructor
public class MarcarNotificacionLeidaApplicationService implements MarcarNotificacionLeidaUseCase {

    private final NotificacionAdminRepository notificacionAdminRepository;

    @Override
    @Transactional
    public void marcar(Long notificacionId) {
        notificacionAdminRepository.marcarComoLeida(notificacionId);
    }
}
