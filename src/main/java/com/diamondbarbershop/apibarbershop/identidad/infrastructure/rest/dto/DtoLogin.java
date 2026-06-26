package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoLogin {
    @NotBlank(message = "El username no puede estar vacío")
    private String username;
    @NotBlank(message = "El password no puede estar vacío")
    private String password;
}
