package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DtoHorarioBarberoInstanciaResponse {
    private LocalDate fecha;
    private String dia;
    private String tipoHorario;
    private String barbero;
}
