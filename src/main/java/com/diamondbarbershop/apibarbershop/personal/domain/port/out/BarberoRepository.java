package com.diamondbarbershop.apibarbershop.personal.domain.port.out;

import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — persistencia del Barbero.
 */
public interface BarberoRepository {

    Barbero save(Barbero barbero);

    Optional<Barbero> findById(Long id);

    /** Solo barberos con estado = 1 (activos). */
    List<Barbero> findActivos();

    boolean existsById(Long id);
}
