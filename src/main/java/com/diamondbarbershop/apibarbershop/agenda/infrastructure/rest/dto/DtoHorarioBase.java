package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DtoHorarioBase {
    private DiaSemana dia;
    private Map<Long, List<Long>> turnosPorTipo;
    // clave: tipoHorarioId (1=Mañana, 2=Tarde, 3=Noche)
    // valor: lista de barberoId asignados para ese turno
}
