package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

import com.diamondbarbershop.apibarbershop.util.DiaSemana;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada — el admin asigna qué barberos trabajan en qué turnos
 * de un día específico de la semana (plantilla base).
 *
 * El input es un Map donde la clave es el ID del TipoHorario (Mañana/Tarde/Noche)
 * y el valor es la lista de IDs de barberos asignados a ese turno.
 */
public interface ActualizarTurnosDiaUseCase {

    void actualizar(ActualizarTurnosDiaCommand command);

    record ActualizarTurnosDiaCommand(
            DiaSemana dia,
            Map<Long, List<Long>> turnosPorTipo
    ) {}
}
