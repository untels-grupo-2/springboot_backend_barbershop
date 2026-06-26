package com.diamondbarbershop.apibarbershop.identidad.domain.port.in;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoRegistro;

/**
 * Puerto de entrada — registrar un usuario nuevo (cliente o admin).
 *
 * El parámetro rolNombre determina el rol asignado al usuario. Los valores
 * usuales son "USER" para clientes y "ADMIN" para administradores.
 */
public interface RegistrarUsuarioUseCase {

    void registrar(DtoRegistro dtoRegistro, String rolNombre);
}
