package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoResetPassword {
    @NotBlank(message = "El campo nueva contraseña no puede estar vacío")
    private String newPassword;
    @NotBlank(message = "El campo confirmación de contraseña no puede estar vacío")
    private String confirmPassword;
    @NotBlank(message = "El campo token password no puede estar vacío")
    private String tokenPassword;
}
