package com.diamondbarbershop.apibarbershop.reservas.domain.port.in;

import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReservaResponse;

import java.util.List;

/**
 * Puerto de entrada — lista las reservas del cliente que llama (/mis-reservas).
 *
 * Para el cliente, no para el admin. El admin tiene /admin con filtros + paginación.
 */
public interface ListarReservasUsuarioUseCase {

    List<DtoReservaResponse> listarPorUsername(String username);
}
