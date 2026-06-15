package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.ListarNotificacionesAdminUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.MarcarNotificacionLeidaUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints para que la app móvil del admin consulte y gestione su buzón
 * de notificaciones (PB-41).
 *
 * Ambos endpoints solo accesibles por usuarios con rol ADMIN —
 * los pushes son específicos del admin, nadie más debería poder leerlos.
 */
@RestController
@RequestMapping("/api/notificaciones/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class NotificacionAdminController {

    private final ListarNotificacionesAdminUseCase listarNotificacionesAdminUseCase;
    private final MarcarNotificacionLeidaUseCase marcarNotificacionLeidaUseCase;

    /**
     * Listado paginado del buzón de notificaciones del admin.
     *
     * Ejemplos:
     *   GET /api/notificaciones/admin
     *     → todas, paginadas (default 20 por página, orden por created_at desc)
     *   GET /api/notificaciones/admin?soloNoLeidas=true&page=0&size=10
     *     → solo no leídas, primeras 10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificacionAdmin>>> listar(
            @RequestParam(required = false, defaultValue = "false") Boolean soloNoLeidas,
            Pageable pageable
    ) {
        Page<NotificacionAdmin> page = listarNotificacionesAdminUseCase.listar(soloNoLeidas, pageable);
        return ResponseEntity.ok(ApiResponse.succes("Lista de notificaciones", page));
    }

    /**
     * Marca una notificación como leída (cuando el admin la abre).
     * Idempotente: marcar dos veces no genera error.
     */
    @PatchMapping("/{id}/leida")
    public ResponseEntity<ApiResponse<Object>> marcarComoLeida(@PathVariable Long id) {
        marcarNotificacionLeidaUseCase.marcar(id);
        return ResponseEntity.ok(ApiResponse.succes("Notificación marcada como leída", null));
    }
}
