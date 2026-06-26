package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreator;
import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreatorSelector;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.CrearBarberoUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service para crear un barbero nuevo.
 *
 * Tras crear y persistir el barbero, dispara el Factory Method de PB-12
 * para generarle la plantilla inicial de HorarioBarberoBase según el tipo
 * elegido por el admin (COMPLETA o FIN_DE_SEMANA).
 *
 * NOTA cross-BC: el BC Personal sigue conociendo el Factory del BC Agenda.
 * Esto es pragmático para evitar overengineering — en el futuro podría
 * desacoplarse con un evento `BarberoCreado` que el BC Agenda escuche.
 */
@Service
@RequiredArgsConstructor
public class CrearBarberoApplicationService implements CrearBarberoUseCase {

    private static final String CARPETA_IMAGENES = "barberos";

    private final BarberoRepository barberoRepository;
    private final SubidorImagen subidorImagen;
    private final HorarioBaseTemplateCreatorSelector horarioBaseTemplateCreatorSelector;

    @Override
    @Transactional
    public Long crear(CrearBarberoCommand command) {
        String urlImagen = null;
        if (command.imagen() != null) {
            urlImagen = subidorImagen.subir(command.imagen(), CARPETA_IMAGENES);
        }

        Barbero barbero = Barbero.crear(command.nombre(), urlImagen);
        Barbero guardado = barberoRepository.save(barbero);

        // Factory Method (PB-12): generar plantilla inicial de horario.
        HorarioBaseTemplateCreator creator =
                horarioBaseTemplateCreatorSelector.seleccionar(command.tipoPlantilla());
        creator.crear(guardado.getId());

        return guardado.getId();
    }
}
