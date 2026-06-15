package com.diamondbarbershop.apibarbershop.reservas.application.listeners;

import com.diamondbarbershop.apibarbershop.emailPassword.service.EmailService;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaCancelada;
import com.diamondbarbershop.apibarbershop.reservas.domain.event.ReservaConfirmada;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEvent;
import com.diamondbarbershop.apibarbershop.shared.domain.event.DomainEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Listener (Observer concreto) — PB-39 backend.
 *
 * Envía email al cliente cuando su reserva cambia de estado:
 *   - ReservaConfirmada → "Tu reserva del {fecha} fue confirmada"
 *   - ReservaCancelada  → "Tu reserva del {fecha} fue cancelada. Motivo: {...}"
 *
 * Resiliencia (decisión de PB-39):
 *   Si el envío de email falla (Gmail caído, dirección inválida, etc.), el
 *   listener captura la excepción y solo la loguea. NO relanza, para evitar
 *   romper la transacción del cambio de estado. La consistencia eventual es
 *   aceptable para notificaciones por email — un correo perdido es mucho
 *   menos grave que impedir que el admin confirme reservas.
 *
 *   En producción real, esto se reemplazaría con una cola asíncrona
 *   (RabbitMQ, AWS SQS) que reintentara el envío automáticamente.
 *
 * Cómo se enchufa:
 *   Es @Component y Spring lo inyecta automáticamente en la List<DomainEventListener>
 *   del ReservaDomainEventPublisher. Sin tocar el publisher ni ningún otro
 *   listener existente — eso es exactamente lo que prometía Observer (PB-13).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionEmailReservaListener implements DomainEventListener {

    private static final String SUBJECT_CONFIRMADA = "Tu reserva fue confirmada — Diamond BarberHub";
    private static final String SUBJECT_CANCELADA  = "Tu reserva fue cancelada — Diamond BarberHub";

    private static final String TEMPLATE_CONFIRMADA = "reserva-confirmada";
    private static final String TEMPLATE_CANCELADA  = "reserva-cancelada";

    private final EmailService emailService;
    private final IdentidadFacade identidadFacade;

    @Override
    public void onEvent(DomainEvent event) {
        try {
            if (event instanceof ReservaConfirmada e) {
                enviarConfirmacion(e);
            } else if (event instanceof ReservaCancelada e) {
                enviarCancelacion(e);
            }
        } catch (Exception ex) {
            // Resiliente: si el envío falla, no rompemos la transacción del
            // cambio de estado de la reserva.
            log.error("No se pudo enviar email de notificación de reserva", ex);
        }
    }

    private void enviarConfirmacion(ReservaConfirmada e) {
        Optional<String> emailCliente = identidadFacade.obtenerEmail(e.clienteId());
        if (emailCliente.isEmpty()) {
            log.warn("No se envía email de confirmación: usuario {} no tiene email registrado",
                    e.clienteId());
            return;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreCliente",
                identidadFacade.obtenerNombre(e.clienteId()).orElse("Cliente"));
        variables.put("fechaReserva", e.fechaReserva().toString());

        emailService.enviarConTemplate(
                emailCliente.get(),
                SUBJECT_CONFIRMADA,
                TEMPLATE_CONFIRMADA,
                variables
        );

        log.info("Email de confirmación enviado a {} (reserva {})", emailCliente.get(), e.reservaId());
    }

    private void enviarCancelacion(ReservaCancelada e) {
        Optional<String> emailCliente = identidadFacade.obtenerEmail(e.clienteId());
        if (emailCliente.isEmpty()) {
            log.warn("No se envía email de cancelación: usuario {} no tiene email registrado",
                    e.clienteId());
            return;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreCliente",
                identidadFacade.obtenerNombre(e.clienteId()).orElse("Cliente"));
        variables.put("fechaReserva", e.fechaReserva().toString());
        variables.put("motivo", e.motivo() != null ? e.motivo() : "");

        emailService.enviarConTemplate(
                emailCliente.get(),
                SUBJECT_CANCELADA,
                TEMPLATE_CANCELADA,
                variables
        );

        log.info("Email de cancelación enviado a {} (reserva {})", emailCliente.get(), e.reservaId());
    }
}
