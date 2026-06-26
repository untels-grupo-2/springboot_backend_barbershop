package com.diamondbarbershop.apibarbershop.catalogo.domain.port.out;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — persistencia del Servicio.
 */
public interface ServicioRepository {

    Servicio save(Servicio servicio);

    Optional<Servicio> findById(Long id);

    /** Solo servicios con estado = 1 (activos). */
    List<Servicio> findActivos();

    boolean existsById(Long id);
}
