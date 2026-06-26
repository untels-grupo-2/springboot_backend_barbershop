package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto;

import lombok.Data;

@Data
public class DtoBarberoDisponible {
    private Long barberoId;
    private String nombre;
    private String urlBarbero;
    private boolean disponible;
}
