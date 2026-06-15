package com.diamondbarbershop.apibarbershop.reservas.application.listeners;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.EnviarPushAdminUseCase;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCreada;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Listener (Observer concreto) — PB-41 backend.
 *
 * Cuando se crea una nueva reserva, dispara una notificación push hacia
 * todos los admins registrados con sus device tokens FCM. La app móvil
 * del admin recibe el push y muestra la notificación en su barra del
 * sistema; al abrir la app, aparece en su sección "Notificaciones".
 *
 * Patrón:
 *   - Observer (PB-13) — reacciona a ReservaCreada sin acoplarse al flujo principal.
 *   - Adapter (vía PushNotificationSender) — el envío real va a FCM, pero el dominio no lo sabe.
 *
 * Resiliencia (consistente con NotificacionEmailReservaListener de PB-39):
 *   Si el envío de push falla, el listener captura la excepción y solo loguea.
 *   NO relanza, para que la reserva ya creada quede persistida — el admin
 *   verá la notificación en su buzón cuando abra la app, aunque no haya
 *   llegado a su barra de notificaciones.
 *
 * Cómo se enchufa:
 *   Spring lo inyecta automáticamente en la List<DomainEventListener> del
 *   ReservaDomainEventPublisher gracias a @Component. Sin modificar ningún
 *   código existente — eso es exactamente lo que promete el patrón Observer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificacionAdminListener implements DomainEventListener {

    private static final String TIPO_RESERVA_CREADA = "RESERVA_CREADA";

    private final EnviarPushAdminUseCase enviarPushAdminUseCase;
    private final IdentidadFacade identidadFacade;

    @Override
    public void onEvent(DomainEvent event) {
        try {
            if (event instanceof ReservaCreada e) {
                disparar(e);
            }
        } catch (Exception ex) {
            log.error("No se pudo enviar push admin para evento {}", event.getClass().getSimpleName(), ex);
            // No relanzo — la reserva ya persistió, el push es best-effort.
        }
    }

    private void disparar(ReservaCreada e) {
        String nombreCliente = identidadFacade.obtenerNombre(e.clienteId())
                .orElse("Un cliente");

        String titulo = "Nueva reserva";
        String cuerpo = String.format(
                "%s reservó para el %s",
                nombreCliente,
                e.fechaReserva()
        );

        enviarPushAdminUseCase.enviar(new EnviarPushAdminUseCase.EnviarPushAdminCommand(
                titulo,
                cuerpo,
                TIPO_RESERVA_CREADA
        ));
    }
}
