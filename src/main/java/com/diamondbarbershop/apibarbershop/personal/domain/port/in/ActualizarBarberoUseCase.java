package com.diamondbarbershop.apibarbershop.personal.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — actualizar datos básicos de un barbero existente.
 * Imagen opcional: si no viene, se conserva la anterior.
 */
public interface ActualizarBarberoUseCase {

    void actualizar(ActualizarBarberoCommand command);

    record ActualizarBarberoCommand(
            Long barberoId,
            String nombre,
            MultipartFile imagen
    ) {}
}
