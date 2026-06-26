package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoUsuario {

    @NotBlank(message = "El campo nombre no puede estar vacío")
    private String nombre;
    @NotBlank(message = "El campo apellido no puede estar vacío")
    private String apellido;
    @NotBlank(message = "El campo email no puede estar vacío")
    private String email;
    @NotBlank(message = "El campo celular no puede estar vacío")
    private String celular;
}
