package com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — persistencia de Valoracion (agregado).
 *
 * Para listados con datos del cliente, ver {@link ValoracionListadoView}
 * y el método buscarParaListado del adapter.
 */
public interface ValoracionRepository {

    Valoracion save(Valoracion valoracion);

    Optional<Valoracion> findById(Long id);

    /** Proyección de listado con nombre y celular del cliente resueltos. */
    List<ValoracionListadoView> buscarParaListado();
}
