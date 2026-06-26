package com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoValoracion {
    @NotNull(message = "El campo valoración no puede estar vacío.")
    private Integer valoracion;
    private Boolean util;
    private String mensaje;
}
