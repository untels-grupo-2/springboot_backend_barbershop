package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

/**
 * Configuración Firebase RESILIENTE (PB-41).
 *
 * Inicializa FirebaseApp solo si la variable de entorno
 * FIREBASE_CREDENTIALS_PATH apunta a un archivo JSON válido.
 *
 * Si no está configurada (caso típico en desarrollo local sin credenciales):
 *   - El bean FirebaseMessaging NO se crea.
 *   - FcmPushNotificationSender detecta esa ausencia y funciona en modo "stub"
 *     (loguea sin enviar).
 *   - La app arranca normal — no rompe el sistema.
 *
 * En producción se configura la variable con la ruta real del archivo
 * `firebase-service-account.json` que se obtiene de la consola de Firebase.
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    /**
     * Bean opcional — devuelve null si las credenciales no están disponibles.
     *
     * Spring permite que un bean retorne null y los consumidores pueden
     * declarar la dependencia como @Autowired(required = false) o como
     * Optional. En este proyecto, FcmPushNotificationSender lo declara
     * como ObjectProvider para chequear su presencia sin fallar al iniciar.
     */
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            log.warn("FIREBASE_CREDENTIALS_PATH no configurada — push notifications en modo STUB " +
                    "(las notificaciones se persistirán pero no se enviarán a dispositivos).");
            return null;
        }

        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(options)
                    : FirebaseApp.getInstance();

            log.info("Firebase inicializado correctamente con credenciales de {}", credentialsPath);
            return FirebaseMessaging.getInstance(app);
        } catch (Exception e) {
            log.error("No se pudo inicializar Firebase con el archivo {}. " +
                    "Push notifications quedarán en modo STUB.", credentialsPath, e);
            return null;
        }
    }
}
