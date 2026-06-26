package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;

import java.util.List;

/**
 * Puerto de entrada — listado de las franjas horarias disponibles para
 * que el cliente arme su reserva.
 */
public interface ListarHorarioRangosUseCase {

    List<HorarioRango> listar();

    HorarioRango obtener(Long id);
}
