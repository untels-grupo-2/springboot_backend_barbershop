package com.diamondbarbershop.apibarbershop.agenda.application.factory;

import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.BarberoJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.HorarioBarberoBaseJpaEntity;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.TipoHorarioJpaEntity;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.persistance.IBarberoJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoBaseJpaRepository;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.ITipoHorarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator concreto: plantilla FIN_DE_SEMANA — solo sábado y domingo.
 *
 * Genera 2 días × N tipos de horario = 2 × 3 = 6 entradas.
 *
 * Pensado para barberos que solo trabajan los fines de semana
 * (medio tiempo, contratos por evento, refuerzos de demanda alta).
 *
 * Demostración del valor del patrón Factory Method:
 *   La diferencia con PlantillaCompletaCreator es UNA SOLA LÍNEA
 *   (el filtro de días). Todo el resto del flujo —cargar barbero, cargar
 *   tipos, persistir— vive en la clase abstracta.
 */
@Component
public class PlantillaFinDeSemanaCreator extends HorarioBaseTemplateCreator {

    /**
     * Días que forman parte de esta plantilla.
     * Si se quisiera otra plantilla similar (ej. SOLO_LUNES_Y_VIERNES),
     * sería una clase nueva con esta lista distinta — sin tocar nada más.
     */
    private static final List<DiaSemana> DIAS_FIN_DE_SEMANA = List.of(
            DiaSemana.SÁBADO,
            DiaSemana.DOMINGO
    );

    public PlantillaFinDeSemanaCreator(
            IBarberoJpaRepository barberoRepository,
            ITipoHorarioJpaRepository tipoHorarioRepository,
            IHorarioBarberoBaseJpaRepository horarioBaseRepository
    ) {
        super(barberoRepository, tipoHorarioRepository, horarioBaseRepository);
    }

    @Override
    protected List<HorarioBarberoBaseJpaEntity> ensamblar(BarberoJpaEntity barbero, List<TipoHorarioJpaEntity> tipos) {
        List<HorarioBarberoBaseJpaEntity> plantilla = new ArrayList<>();
        for (DiaSemana dia : DIAS_FIN_DE_SEMANA) {
            for (TipoHorarioJpaEntity tipo : tipos) {
                plantilla.add(construirEntrada(barbero, tipo, dia));
            }
        }
        return plantilla;
    }

    private HorarioBarberoBaseJpaEntity construirEntrada(BarberoJpaEntity barbero, TipoHorarioJpaEntity tipo, DiaSemana dia) {
        HorarioBarberoBaseJpaEntity entrada = new HorarioBarberoBaseJpaEntity();
        entrada.setBarbero(barbero);
        entrada.setTipoHorario(tipo);
        entrada.setDia(dia);
        entrada.setEst_id(null);
        entrada.setEstado(1);
        return entrada;
    }
}
