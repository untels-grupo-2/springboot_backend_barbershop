package com.diamondbarbershop.apibarbershop.personal.infrastructure.rest.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class DtoBarberoResponse {
    private Long barbero_id;
    private String nombre;
    private String urlBarbero;
}
