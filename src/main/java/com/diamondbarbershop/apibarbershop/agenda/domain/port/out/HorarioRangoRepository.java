package com.diamondbarbershop.apibarbershop.agenda.domain.port.out;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — persistencia de HorarioRango (franjas horarias).
 */
public interface HorarioRangoRepository {

    List<HorarioRango> findAll();

    Optional<HorarioRango> findById(Long id);
}
