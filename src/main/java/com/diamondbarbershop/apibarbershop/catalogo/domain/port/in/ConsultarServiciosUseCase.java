package com.diamondbarbershop.apibarbershop.catalogo.domain.port.in;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;

import java.util.List;

/**
 * Puerto de entrada — lectura de servicios.
 * Agrupa los dos casos de consulta (listAll + readOne) en una sola interfaz
 * porque son operaciones afines y caben en el mismo @Service.
 */
public interface ConsultarServiciosUseCase {

    /** Solo servicios activos. Para listados de cliente y admin. */
    List<Servicio> listarActivos();

    /** Detalle por ID. Lanza si no existe. */
    Servicio obtener(Long id);
}
