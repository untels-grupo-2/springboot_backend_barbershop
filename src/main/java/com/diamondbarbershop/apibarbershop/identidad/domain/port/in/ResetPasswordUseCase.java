package com.diamondbarbershop.apibarbershop.identidad.domain.port.in;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoResetPassword;

/**
 * Puerto de entrada — cambiar la contraseña usando el token enviado por email.
 */
public interface ResetPasswordUseCase {

    void resetear(DtoResetPassword dtoResetPassword);
}
