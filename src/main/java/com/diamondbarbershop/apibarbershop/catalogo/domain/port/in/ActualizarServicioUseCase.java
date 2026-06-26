package com.diamondbarbershop.apibarbershop.catalogo.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — actualizar un servicio existente.
 *
 * La imagen es opcional: si no se envía nueva, conserva la anterior.
 */
public interface ActualizarServicioUseCase {

    void actualizar(ActualizarServicioCommand command);

    record ActualizarServicioCommand(
            Long servicioId,
            String nombre,
            Long precio,
            String descripcion,
            Long tipoServicioId,
            MultipartFile imagen        // puede ser null
    ) {}
}
