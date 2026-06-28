package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.RegistrarDeviceTokenUseCase.RegistrarDeviceTokenCommand;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.DeviceTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarDeviceTokenApplicationServiceTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @InjectMocks
    private RegistrarDeviceTokenApplicationService service;

    @Test
    @DisplayName("Debe delegar al repositorio para guardar o actualizar el token")
    void should_delegateToRepository_when_registrarIsCalled() {
        RegistrarDeviceTokenCommand command = new RegistrarDeviceTokenCommand(5L, "fcm-token-xyz", "ANDROID");

        service.registrar(command);

        verify(deviceTokenRepository).guardarOActualizar(5L, "fcm-token-xyz", "ANDROID");
    }
}
