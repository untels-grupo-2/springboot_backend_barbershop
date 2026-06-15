package com.diamondbarbershop.apibarbershop.agenda.application.factory;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.TipoPlantillaHorario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Selector — punto único de decisión para escoger qué Creator usar
 * según el TipoPlantillaHorario que el administrador eligió al crear
 * el barbero (PB-12).
 *
 * Concentrar la decisión aquí mantiene el principio de responsabilidad
 * única: el BarberoService orquesta, los Creators ensamblan, este
 * Selector decide.
 *
 * Beneficio del switch exhaustivo de Java:
 *   El compilador detecta en tiempo de compilación si agrego un valor
 *   nuevo al enum y olvido manejarlo aquí. No hay forma de que un caso
 *   se pierda silenciosamente.
 *
 * Para agregar una nueva plantilla:
 *   1. Agregar el valor al enum TipoPlantillaHorario.
 *   2. Crear una nueva subclase de HorarioBaseTemplateCreator.
 *   3. Agregar el case correspondiente aquí.
 *   No se modifica nada más (BarberoService, los Creators existentes,
 *   ni HorarioBaseTemplateCreator).
 */
@Component
@RequiredArgsConstructor
public class HorarioBaseTemplateCreatorSelector {

    private final PlantillaCompletaCreator plantillaCompletaCreator;
    private final PlantillaFinDeSemanaCreator plantillaFinDeSemanaCreator;

    /**
     * Devuelve el Creator correspondiente al tipo solicitado.
     */
    public HorarioBaseTemplateCreator seleccionar(TipoPlantillaHorario tipo) {
        return switch (tipo) {
            case COMPLETA      -> plantillaCompletaCreator;
            case FIN_DE_SEMANA -> plantillaFinDeSemanaCreator;
        };
    }
}
