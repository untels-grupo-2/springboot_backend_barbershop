package com.diamondbarbershop.apibarbershop.catalogo.domain.port.in;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;

import java.util.List;

/**
 * Puerto de entrada — listado de tipos de servicio (Corte, Barba, etc.)
 * usado por el frontend al construir el formulario de crear/actualizar servicio.
 */
public interface ListarTiposServicioUseCase {

    List<TipoServicio> listar();
}
