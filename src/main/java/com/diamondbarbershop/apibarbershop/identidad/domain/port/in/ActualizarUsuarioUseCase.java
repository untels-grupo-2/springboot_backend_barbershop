package com.diamondbarbershop.apibarbershop.identidad.domain.port.in;

import com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest.dto.DtoUsuario;
import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — actualización de datos del usuario.
 */
public interface ActualizarUsuarioUseCase {

    void actualizar(Long usuarioId, DtoUsuario dtoUsuario, MultipartFile imagen);

    void actualizarPorUsername(String username, DtoUsuario dtoUsuario, MultipartFile imagen);
}
