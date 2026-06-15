package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.EnviarPushAdminUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.DeviceTokenRepository;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.PushNotificationSender;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service que orquesta el envío de un push a TODOS los admins (PB-41).
 *
 * Flujo:
 *   1. PERSISTIR la notificación en `notificaciones_admin` (siempre).
 *      Eso garantiza que el admin la verá en su app aunque el envío FCM falle.
 *   2. RESOLVER los IDs de usuarios con rol ADMIN vía IdentidadFacade.
 *   3. OBTENER los device tokens FCM de esos usuarios.
 *   4. ENVIAR el push vía FCM (best effort — si falla, no rompe nada).
 *
 * Sobre el rol "ADMIN":
 *   El nombre del rol está hardcodeado aquí como constante. Si en el futuro
 *   el negocio cambia el nombre del rol o se introduce un sistema más fino
 *   de permisos, este es el único punto que se modifica.
 */
@Service
@RequiredArgsConstructor
public class EnviarPushAdminApplicationService implements EnviarPushAdminUseCase {

    /** Nombre del rol que recibe estas notificaciones — debe coincidir con el rol seedeado en BD. */
    private static final String ROL_ADMIN = "ADMIN";

    private final NotificacionAdminRepository notificacionAdminRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PushNotificationSender pushNotificationSender;
    private final IdentidadFacade identidadFacade;

    @Override
    @Transactional
    public void enviar(EnviarPushAdminCommand command) {
        // 1. Persistir la notificación SIEMPRE — la app móvil la verá en su buzón
        //    aunque FCM esté caído.
        notificacionAdminRepository.guardar(
                command.titulo(),
                command.cuerpo(),
                command.tipo()
        );

        // 2. Resolver a quién enviar el push.
        List<Long> idsAdmins = identidadFacade.obtenerIdsUsuariosConRol(ROL_ADMIN);

        // 3. Obtener los tokens FCM de esos usuarios.
        List<String> tokens = deviceTokenRepository.obtenerTokensDeUsuarios(idsAdmins);

        // 4. Enviar el push. El sender es BEST-EFFORT: si Firebase está caído
        //    o no configurado, loguea pero no lanza excepción.
        pushNotificationSender.enviarATodos(tokens, command.titulo(), command.cuerpo());
    }
}
