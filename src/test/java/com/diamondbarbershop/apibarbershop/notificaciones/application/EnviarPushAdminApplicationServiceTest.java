package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.EnviarPushAdminUseCase.EnviarPushAdminCommand;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.DeviceTokenRepository;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdmin;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.PushNotificationSender;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.IdentidadFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnviarPushAdminApplicationServiceTest {

    @Mock
    private NotificacionAdminRepository notificacionAdminRepository;

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private PushNotificationSender pushNotificationSender;

    @Mock
    private IdentidadFacade identidadFacade;

    @InjectMocks
    private EnviarPushAdminApplicationService service;

    private final EnviarPushAdminCommand command = new EnviarPushAdminCommand(
            "Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA"
    );

    @Test
    @DisplayName("Debe guardar notificacion, resolver admins y enviar push exitosamente")
    void should_saveAndSendPush_when_allStepsSucceed() {
        NotificacionAdmin notificacion = new NotificacionAdmin(
                1L, "Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA", false, LocalDateTime.now()
        );
        when(notificacionAdminRepository.guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA"))
                .thenReturn(notificacion);
        when(identidadFacade.obtenerIdsUsuariosConRol("ADMIN")).thenReturn(List.of(1L, 2L));
        when(deviceTokenRepository.obtenerTokensDeUsuarios(List.of(1L, 2L)))
                .thenReturn(List.of("token-abc", "token-def"));

        service.enviar(command);

        verify(notificacionAdminRepository).guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA");
        verify(identidadFacade).obtenerIdsUsuariosConRol("ADMIN");
        verify(deviceTokenRepository).obtenerTokensDeUsuarios(List.of(1L, 2L));
        verify(pushNotificationSender).enviarATodos(List.of("token-abc", "token-def"), "Nueva reserva", "Se ha creado una nueva reserva");
    }

    @Test
    @DisplayName("Debe guardar notificacion pero no enviar push cuando no hay device tokens")
    void should_saveButNotSendPush_when_noDeviceTokens() {
        NotificacionAdmin notificacion = new NotificacionAdmin(
                1L, "Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA", false, LocalDateTime.now()
        );
        when(notificacionAdminRepository.guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA"))
                .thenReturn(notificacion);
        when(identidadFacade.obtenerIdsUsuariosConRol("ADMIN")).thenReturn(List.of(1L));
        when(deviceTokenRepository.obtenerTokensDeUsuarios(List.of(1L)))
                .thenReturn(Collections.emptyList());

        service.enviar(command);

        verify(notificacionAdminRepository).guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA");
        verify(pushNotificationSender).enviarATodos(Collections.emptyList(), "Nueva reserva", "Se ha creado una nueva reserva");
    }

    @Test
    @DisplayName("Debe guardar notificacion incluso cuando el envio push falla")
    void should_saveNotification_when_pushSenderThrows() {
        NotificacionAdmin notificacion = new NotificacionAdmin(
                1L, "Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA", false, LocalDateTime.now()
        );
        when(notificacionAdminRepository.guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA"))
                .thenReturn(notificacion);
        when(identidadFacade.obtenerIdsUsuariosConRol("ADMIN")).thenReturn(List.of(1L));
        when(deviceTokenRepository.obtenerTokensDeUsuarios(List.of(1L)))
                .thenReturn(List.of("token-abc"));
        doThrow(new RuntimeException("FCM esta caido"))
                .when(pushNotificationSender).enviarATodos(List.of("token-abc"), "Nueva reserva", "Se ha creado una nueva reserva");

        try {
            service.enviar(command);
        } catch (RuntimeException ignored) {
        }

        verify(notificacionAdminRepository).guardar("Nueva reserva", "Se ha creado una nueva reserva", "RESERVA_CREADA");
    }
}
