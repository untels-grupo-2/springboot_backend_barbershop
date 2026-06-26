package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoBarberoDisponible;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de entrada — lista qué barberos están disponibles para reservar
 * en una fecha, tipo de horario y rango específicos.
 *
 * Cruza información de:
 *   - HorarioBarberoInstancia (BC Agenda) → quiénes trabajan ese día/tipo
 *   - Reservas activas (BC Reservas vía ConsultarReservasActivasPort) → quiénes ya están reservados
 */
public interface ListarBarberosDisponiblesUseCase {

    List<DtoBarberoDisponible> listar(LocalDate fecha, Long tipoHorarioId, Long horarioRangoId);
}
