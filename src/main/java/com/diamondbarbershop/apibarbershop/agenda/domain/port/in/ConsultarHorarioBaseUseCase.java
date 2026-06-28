package com.diamondbarbershop.apibarbershop.agenda.domain.port.in;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;

import java.util.List;
import java.util.Map;

public interface ConsultarHorarioBaseUseCase {

    Map<DiaSemana, List<HorarioBaseView>> listarAgrupadoPorDia();
}
