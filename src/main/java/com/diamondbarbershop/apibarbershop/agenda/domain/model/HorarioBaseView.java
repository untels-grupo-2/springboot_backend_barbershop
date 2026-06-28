package com.diamondbarbershop.apibarbershop.agenda.domain.model;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;

public record HorarioBaseView(
        Long horarioBaseId,
        Long barberoId,
        String barberoNombre,
        Long tipoHorarioId,
        String tipoHorarioNombre,
        DiaSemana dia,
        boolean asignado
) {}
