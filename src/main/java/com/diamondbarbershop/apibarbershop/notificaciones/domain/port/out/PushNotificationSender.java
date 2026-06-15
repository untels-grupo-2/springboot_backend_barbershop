package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out;

import java.util.List;

/**
 * Puerto de salida — envío real de notificaciones push a dispositivos.
 *
 * Este puerto es el "Adapter pattern" aplicado a FCM: el dominio define el
 * contrato simple ("manda este texto a estos tokens") y el adapter de
 * infraestructura (FcmPushNotificationSender) lo traduce a llamadas reales
 * al SDK de Firebase.
 *
 * Beneficios:
 *   - El dominio no conoce Firebase.
 *   - Si mañana se cambia FCM por otro proveedor (OneSignal, Pusher, AWS SNS),
 *     solo cambia el adapter — el resto del sistema no se entera.
 *   - Para tests, se puede sustituir por un fake que solo loguea.
 */
public interface PushNotificationSender {

    /**
     * Envía la notificación push a TODOS los tokens listados, en paralelo si
     * el proveedor lo soporta.
     *
     * Es BEST-EFFORT: si el envío falla a algún token (token expirado, dispositivo
     * desinstalado, FCM caído, etc.), la implementación NO lanza excepción —
     * solo loguea internamente. El llamador asume éxito siempre.
     *
     * Si la lista de tokens está vacía, no hace nada (no es error).
     */
    void enviarATodos(List<String> tokens, String titulo, String cuerpo);
}
