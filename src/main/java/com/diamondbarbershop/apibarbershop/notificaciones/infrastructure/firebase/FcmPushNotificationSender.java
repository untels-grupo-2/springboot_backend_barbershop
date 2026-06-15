package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.firebase;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.PushNotificationSender;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter — implementa el puerto PushNotificationSender usando Firebase
 * Cloud Messaging.
 *
 * Modo STUB:
 *   Si Firebase no pudo inicializarse (la variable FIREBASE_CREDENTIALS_PATH
 *   no está configurada o el archivo no es válido), el bean FirebaseMessaging
 *   es null y este adapter funciona en modo "stub" — solo loguea sin enviar.
 *   Esto permite que devs trabajen localmente sin credenciales reales.
 *
 * Resilencia:
 *   Si Firebase está activo pero el envío falla (token inválido, FCM caído,
 *   etc.), no se propaga la excepción. Es BEST-EFFORT. Las notificaciones
 *   igual quedaron persistidas en `notificaciones_admin`, así que el admin
 *   las verá cuando abra la app.
 */
@Component
@Slf4j
public class FcmPushNotificationSender implements PushNotificationSender {

    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    public FcmPushNotificationSender(ObjectProvider<FirebaseMessaging> firebaseMessagingProvider) {
        this.firebaseMessagingProvider = firebaseMessagingProvider;
    }

    @Override
    public void enviarATodos(List<String> tokens, String titulo, String cuerpo) {
        if (tokens == null || tokens.isEmpty()) {
            log.debug("Sin tokens registrados para enviar push '{}'", titulo);
            return;
        }

        FirebaseMessaging messaging = firebaseMessagingProvider.getIfAvailable();
        if (messaging == null) {
            // Modo stub — credenciales no configuradas.
            log.info("[FCM-STUB] Push omitido (Firebase no inicializado). titulo='{}', tokens={}",
                    titulo, tokens.size());
            return;
        }

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .addAllTokens(tokens)
                    .build();

            var response = messaging.sendEachForMulticast(message);
            log.info("Push enviado: {} OK / {} fallidos (titulo='{}')",
                    response.getSuccessCount(), response.getFailureCount(), titulo);

            // Loguear fallos individuales para diagnóstico, pero no propagar.
            if (response.getFailureCount() > 0) {
                response.getResponses().forEach(r -> {
                    if (!r.isSuccessful()) {
                        log.warn("FCM falló para un token: {}", r.getException().getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error enviando push notification via FCM (titulo='{}')", titulo, e);
        }
    }
}
