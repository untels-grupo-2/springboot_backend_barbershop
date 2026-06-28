package com.diamondbarbershop.apibarbershop.agenda.domain.port.out;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoInstancia;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida del contexto Agenda.
 * Define el contrato para consultar horarios concretos de barberos.
 */
public interface HorarioInstanciaRepository {

    /** Todas las instancias de un barbero para una fecha dada. */
    List<HorarioBarberoInstancia> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha);

    HorarioBarberoInstancia save(HorarioBarberoInstancia instancia);

    void deleteByFechaBetween(LocalDate desde, LocalDate hasta);
}
