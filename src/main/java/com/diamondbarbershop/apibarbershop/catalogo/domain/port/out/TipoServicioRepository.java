package com.diamondbarbershop.apibarbershop.catalogo.domain.port.out;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — persistencia de TipoServicio (lectura por ahora; no se
 * crean tipos vía API, son datos seed).
 */
public interface TipoServicioRepository {

    Optional<TipoServicio> findById(Long id);

    List<TipoServicio> findAll();
}
