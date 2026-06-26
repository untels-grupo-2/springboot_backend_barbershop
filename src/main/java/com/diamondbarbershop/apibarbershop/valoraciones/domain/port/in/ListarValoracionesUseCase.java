package com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionListadoView;

import java.util.List;

/**
 * Puerto de entrada — listado de valoraciones con datos del cliente.
 *
 * Retorna una proyección (Read Model) que incluye el nombre y celular del
 * cliente ya resueltos via JOIN — para evitar N+1.
 */
public interface ListarValoracionesUseCase {

    List<ValoracionListadoView> listar();
}
