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
 * Creator concreto: plantilla COMPLETA — semana entera.
 *
 * Genera 7 días × N tipos de horario = 7 × 3 = 21 entradas
 * (asumiendo los 3 tipos de horario actuales: mañana, tarde, noche).
 *
 * Cada entrada inicia con est_id = null (el barbero NO trabaja por defecto;
 * el administrador activa los turnos que correspondan después).
 *
 * Es el comportamiento por defecto — replica el método legacy
 * HorarioBarberoBaseService.crearHorarioBaseInicial() que se elimina con
 * la entrega de PB-12.
 */
@Component
public class PlantillaCompletaCreator extends HorarioBaseTemplateCreator {

    public PlantillaCompletaCreator(
            IBarberoJpaRepository barberoRepository,
            ITipoHorarioJpaRepository tipoHorarioRepository,
            IHorarioBarberoBaseJpaRepository horarioBaseRepository
    ) {
        super(barberoRepository, tipoHorarioRepository, horarioBaseRepository);
    }

    @Override
    protected List<HorarioBarberoBaseJpaEntity> ensamblar(BarberoJpaEntity barbero, List<TipoHorarioJpaEntity> tipos) {
        List<HorarioBarberoBaseJpaEntity> plantilla = new ArrayList<>();
        for (DiaSemana dia : DiaSemana.values()) {
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
        entrada.setEst_id(null);   // null = no trabaja por defecto
        entrada.setEstado(1);      // 1 = activo (puede ser asignado)
        return entrada;
    }
}
