package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBarberoInstanciaResponse;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada — devuelve las instancias de la semana actual agrupadas
 * por día. Pensado para la vista de calendario del admin.
 *
 * Retorna directamente el DTO HTTP porque la vista es agrupada y necesita
 * nombres resueltos — equivalente a un Read Model con proyección.
 */
public interface ConsultarHorarioSemanaActualUseCase {

    Map<String, List<DtoHorarioBarberoInstanciaResponse>> consultarSemanaAgrupada();
}
