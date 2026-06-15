package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out;

import java.util.List;

/**
 * Puerto de salida — persistencia de tokens FCM.
 */
public interface DeviceTokenRepository {

    /**
     * Guarda un token nuevo, o actualiza la `updated_at` si el token ya existe
     * (idempotente — la app móvil puede llamar al endpoint en cada login).
     */
    void guardarOActualizar(Long usuarioId, String token, String plataforma);

    /**
     * Devuelve todos los tokens FCM de los usuarios listados.
     * Si la lista de IDs está vacía o ninguno tiene tokens, devuelve lista vacía.
     */
    List<String> obtenerTokensDeUsuarios(List<Long> usuarioIds);
}
