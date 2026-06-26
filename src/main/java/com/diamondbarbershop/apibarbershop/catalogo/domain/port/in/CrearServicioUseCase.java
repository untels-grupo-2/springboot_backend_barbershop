package com.diamondbarbershop.apibarbershop.catalogo.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — crear un servicio nuevo en el catálogo.
 *
 * Recibe los datos básicos y una imagen opcional. El adapter de imagen
 * (Cloudinary u otro) la sube y devuelve URL; el use case persiste todo.
 */
public interface CrearServicioUseCase {

    Long crear(CrearServicioCommand command);

    record CrearServicioCommand(
            String nombre,
            Long precio,
            String descripcion,
            Long tipoServicioId,
            MultipartFile imagen
    ) {}
}
