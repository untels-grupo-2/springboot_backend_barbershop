package com.diamondbarbershop.apibarbershop.identidad.domain.port.in;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuarioResponse;

import java.util.List;

/**
 * Puerto de entrada — consultas de usuario (admin views + perfil del usuario actual).
 */
public interface ConsultarUsuariosUseCase {

    /** Lista todos los clientes (rol USER). */
    List<DtoUsuarioResponse> listarClientes();

    DtoUsuarioResponse obtener(Long id);

    DtoUsuarioResponse obtenerPorUsername(String username);
}
