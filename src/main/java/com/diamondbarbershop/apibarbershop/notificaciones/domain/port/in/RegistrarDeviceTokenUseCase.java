package com.diamondbarbershop.apibarbershop.notificaciones.domain.port.in;

/**
 * Puerto de entrada — registrar (o actualizar) el token FCM de un dispositivo
 * de un usuario.
 *
 * La app móvil llama a este caso de uso al iniciar sesión, una vez que Firebase
 * le entrega el token del dispositivo. Si el mismo dispositivo se reinstala,
 * el token cambia y se vuelve a llamar — el caso de uso debe ser idempotente.
 */
public interface RegistrarDeviceTokenUseCase {

    void registrar(RegistrarDeviceTokenCommand command);

    record RegistrarDeviceTokenCommand(
            Long usuarioId,
            String token,
            String plataforma   // ANDROID, IOS, WEB
    ) {}
}
