package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in;

/**
 * Puerto de entrada — disparar el envío de una notificación push a TODOS los admins.
 *
 * Consumido principalmente por el PushNotificacionAdminListener del BC Reservas
 * (Observer del patrón PB-13). Pero podría usarse desde cualquier flujo que
 * necesite avisar al admin.
 *
 * El caso de uso:
 *   1. Persiste la notificación en `notificaciones_admin` (siempre).
 *   2. Resuelve los device tokens de los usuarios con rol ADMIN.
 *   3. Envía el push vía FCM (best effort — si falla, queda persistida).
 */
public interface EnviarPushAdminUseCase {

    void enviar(EnviarPushAdminCommand command);

    record EnviarPushAdminCommand(
            String titulo,
            String cuerpo,
            String tipo    // ej. RESERVA_CREADA — la app móvil puede filtrar/renderizar por tipo
    ) {}
}
