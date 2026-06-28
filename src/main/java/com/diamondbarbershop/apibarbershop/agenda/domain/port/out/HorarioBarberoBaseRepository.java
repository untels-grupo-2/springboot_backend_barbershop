package com.diamondbarbershop.apibarbershop.agenda.domain.port.out;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;

import java.util.List;

/**
 * Puerto de salida — persistencia de HorarioBarberoBase (plantilla semanal).
 */
public interface HorarioBarberoBaseRepository {

    List<HorarioBarberoBase> findByDia(DiaSemana dia);

    List<HorarioBarberoBase> findActivos();

    List<HorarioBaseView> findAllActivosComoView();

    void saveAll(List<HorarioBarberoBase> registros);
}
