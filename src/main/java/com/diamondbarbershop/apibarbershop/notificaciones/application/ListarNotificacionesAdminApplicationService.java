package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.ListarNotificacionesAdminUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para listar las notificaciones que el admin tiene en su
 * buzón (PB-41).
 *
 * Soporta filtro opcional por "solo no leídas" — la app móvil normalmente
 * abre con todas y muestra un badge con el conteo de no leídas.
 */
@Service
@RequiredArgsConstructor
public class ListarNotificacionesAdminApplicationService implements ListarNotificacionesAdminUseCase {

    private final NotificacionAdminRepository notificacionAdminRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionAdmin> listar(Boolean soloNoLeidas, Pageable pageable) {
        return notificacionAdminRepository.listar(soloNoLeidas, pageable);
    }
}
