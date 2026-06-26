package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoRefreshToken {
    @NotBlank(message = "El refresh token no puede estar vacío")
    private String refreshToken;
}
