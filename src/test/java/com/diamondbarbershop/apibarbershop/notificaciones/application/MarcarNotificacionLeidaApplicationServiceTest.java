package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.NotificacionAdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarcarNotificacionLeidaApplicationServiceTest {

    @Mock
    private NotificacionAdminRepository notificacionAdminRepository;

    @InjectMocks
    private MarcarNotificacionLeidaApplicationService service;

    @Test
    @DisplayName("Debe delegar al repositorio para marcar la notificacion como leida")
    void should_delegateToRepository_when_marcarIsCalled() {
        Long notificacionId = 42L;

        service.marcar(notificacionId);

        verify(notificacionAdminRepository).marcarComoLeida(42L);
    }
}
