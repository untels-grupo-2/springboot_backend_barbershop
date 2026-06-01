package com.diamondbarbershop.apibarbershop.dtos.valoracion.response;

import lombok.Data;

@Data
public class DtoValoracionResponse {
    private Long valoracion_id;
    private Long usuarioId;
    private String celular;
    private Integer valoracion;
    private Boolean util;
    private String mensaje;
    private String usuario_nombre;
    private Integer estado;
}
