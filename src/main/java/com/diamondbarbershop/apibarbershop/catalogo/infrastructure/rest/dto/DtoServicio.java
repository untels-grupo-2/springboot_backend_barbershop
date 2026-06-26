package com.diamondbarbershop.apibarbershop.catalogo.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoServicio {
    @NotBlank(message = "El campo nombre no puede estar vacío")
    private String nombre;
    @NotNull(message = "El campo precio no puede estar vacío")
    private Long precio;
    @NotBlank(message = "El campo descripción no puede estar vacío")
    private String descripcion;
    @NotNull(message = "El campo tipoServicio no puede estar vacío")
    private Long tipoServicio_id;
}
