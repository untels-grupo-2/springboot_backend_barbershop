package com.diamondbarbershop.apibarbershop.personal.domain.port.in;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.TipoPlantillaHorario;
import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada — crear un barbero nuevo.
 *
 * Recibe los datos básicos, una imagen opcional y el tipo de plantilla
 * de horario inicial a generar (PB-12).
 */
public interface CrearBarberoUseCase {

    Long crear(CrearBarberoCommand command);

    record CrearBarberoCommand(
            String nombre,
            MultipartFile imagen,                       // opcional
            TipoPlantillaHorario tipoPlantilla         // PB-12 — Factory Method
    ) {}
}
