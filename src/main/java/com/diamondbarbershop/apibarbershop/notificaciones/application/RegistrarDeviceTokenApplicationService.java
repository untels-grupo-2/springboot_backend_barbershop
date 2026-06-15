package com.diamondbarbershop.apibarbershop.notificaciones.application;

import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in.RegistrarDeviceTokenUseCase;
import com.diamondbarbershop.apibarbershop.notificaciones.domain.port.out.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para registrar un token FCM (PB-41).
 *
 * La app móvil llama a este caso de uso al iniciar sesión, una vez que
 * Firebase le entrega el token del dispositivo. El método es idempotente —
 * si el token ya existe en BD, solo se actualizan los metadatos (usuario,
 * timestamp).
 */
@Service
@RequiredArgsConstructor
public class RegistrarDeviceTokenApplicationService implements RegistrarDeviceTokenUseCase {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    @Transactional
    public void registrar(RegistrarDeviceTokenCommand command) {
        deviceTokenRepository.guardarOActualizar(
                command.usuarioId(),
                command.token(),
                command.plataforma()
        );
    }
}
