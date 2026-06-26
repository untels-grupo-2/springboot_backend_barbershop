package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto;

import lombok.Data;

@Data
public class DtoHorarioRangoResponse {
    private Long horarioRango_id;
    private String rango;
    private String tipoHorario;
}
