package com.diamondbarbershop.apibarbershop.agenda.application.factory;

import com.diamondbarbershop.apibarbershop.exceptions.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.exceptions.TipoHorarioNoEncotradoException;
import com.diamondbarbershop.apibarbershop.models.Barbero;
import com.diamondbarbershop.apibarbershop.models.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.models.TipoHorario;
import com.diamondbarbershop.apibarbershop.repositories.IBarberoRepository;
import com.diamondbarbershop.apibarbershop.repositories.IHorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.repositories.ITipoHorarioRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Factory Method (GoF — Creacional) — PB-12.
 *
 * Clase abstracta que define el FLUJO COMÚN para generar la plantilla
 * inicial de horario de un barbero. Las subclases solo deciden CÓMO
 * ensamblar la plantilla concreta — los días y combinaciones a crear.
 *
 * Patrón en juego:
 *   - Template Method: el flujo "cargar dependencias → ensamblar → persistir"
 *     está fijo en crear().
 *   - Factory Method: ensamblar() es el método de fábrica que las subclases
 *     redefinen para producir el tipo concreto de plantilla.
 *
 * Beneficio Open/Closed:
 *   Agregar una nueva plantilla (ej. PlantillaSoloTardesCreator) = nueva
 *   subclase + agregar el case en el Selector. No se toca esta clase ni
 *   las subclases existentes.
 *
 * NOTA TRANSITORIA:
 *   Hoy esta clase consume los repositorios JPA legacy del BC Agenda
 *   (IHorarioBarberoBaseRepository, IBarberoRepository, ITipoHorarioRepository).
 *   Cuando se complete la migración hexagonal del BC Agenda en Sprint 4,
 *   estos repos se reemplazarán por puertos de salida propios del BC.
 *   La API pública (crear(), ensamblar()) no cambia.
 */
@RequiredArgsConstructor
public abstract class HorarioBaseTemplateCreator {

    protected final IBarberoRepository barberoRepository;
    protected final ITipoHorarioRepository tipoHorarioRepository;
    protected final IHorarioBarberoBaseRepository horarioBaseRepository;

    /**
     * Template Method — flujo fijo de creación.
     * No se sobrescribe; las subclases solo redefinen ensamblar().
     */
    public final void crear(Long barberoId) {
        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new BarberoNoEncontradoException(
                        MensajeError.BARBERO_NO_ENCONTRADO));

        List<TipoHorario> tipos = tipoHorarioRepository.findAll();
        if (tipos.isEmpty()) {
            throw new TipoHorarioNoEncotradoException(
                    MensajeError.TIPO_HORARIO_NO_ENCOTRADO);
        }

        List<HorarioBarberoBase> plantilla = ensamblar(barbero, tipos);

        horarioBaseRepository.saveAll(plantilla);
    }

    /**
     * Factory Method — cada subclase decide qué combinaciones día/turno
     * forman parte de su plantilla.
     *
     * @param barbero   barbero al que se le asigna la plantilla
     * @param tipos     todos los TipoHorario disponibles en el sistema
     * @return lista de HorarioBarberoBase listos para persistir
     */
    protected abstract List<HorarioBarberoBase> ensamblar(
            Barbero barbero,
            List<TipoHorario> tipos
    );
}
