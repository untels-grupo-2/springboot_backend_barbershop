package com.diamondbarbershop.apibarbershop.agenda.application.factory;

import com.diamondbarbershop.apibarbershop.models.Barbero;
import com.diamondbarbershop.apibarbershop.models.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.models.TipoHorario;
import com.diamondbarbershop.apibarbershop.repositories.IBarberoRepository;
import com.diamondbarbershop.apibarbershop.repositories.IHorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.repositories.ITipoHorarioRepository;
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
            IBarberoRepository barberoRepository,
            ITipoHorarioRepository tipoHorarioRepository,
            IHorarioBarberoBaseRepository horarioBaseRepository
    ) {
        super(barberoRepository, tipoHorarioRepository, horarioBaseRepository);
    }

    @Override
    protected List<HorarioBarberoBase> ensamblar(Barbero barbero, List<TipoHorario> tipos) {
        List<HorarioBarberoBase> plantilla = new ArrayList<>();
        for (DiaSemana dia : DiaSemana.values()) {
            for (TipoHorario tipo : tipos) {
                plantilla.add(construirEntrada(barbero, tipo, dia));
            }
        }
        return plantilla;
    }

    private HorarioBarberoBase construirEntrada(Barbero barbero, TipoHorario tipo, DiaSemana dia) {
        HorarioBarberoBase entrada = new HorarioBarberoBase();
        entrada.setBarbero(barbero);
        entrada.setTipoHorario(tipo);
        entrada.setDia(dia);
        entrada.setEst_id(null);   // null = no trabaja por defecto
        entrada.setEstado(1);      // 1 = activo (puede ser asignado)
        return entrada;
    }
}
