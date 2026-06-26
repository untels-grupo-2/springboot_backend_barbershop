package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto;

import lombok.Data;

@Data
public class DtoUsuarioResponse {
    private Long usuario_id;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private String urlUsuario;
}
