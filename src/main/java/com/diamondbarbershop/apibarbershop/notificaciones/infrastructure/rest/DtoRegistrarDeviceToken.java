package com.diamondbarbershop.apibarbershop.notificaciones.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para POST /api/notificaciones/device-token.
 *
 * El usuarioId NO viene en el body — se resuelve del Authentication
 * (Spring Security garantiza que el endpoint está autenticado).
 */
@Data
public class DtoRegistrarDeviceToken {

    @NotBlank(message = "El campo token no puede estar vacío")
    private String token;

    @NotBlank(message = "El campo plataforma no puede estar vacío")
    private String plataforma;   // ANDROID, IOS, WEB
}
