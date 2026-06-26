package com.diamondbarbershop.apibarbershop.personal.domain.port.in;

import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;

import java.util.List;

/**
 * Puerto de entrada — consultas de barberos. Agrupa listAll + readOne.
 */
public interface ConsultarBarberosUseCase {

    List<Barbero> listarActivos();

    Barbero obtener(Long id);
}
