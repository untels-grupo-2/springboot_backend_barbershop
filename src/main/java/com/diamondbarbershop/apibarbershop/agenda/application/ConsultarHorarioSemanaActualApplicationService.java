package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConsultarHorarioSemanaActualUseCase;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.persistance.IHorarioBarberoInstanciaQueryAdapter;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBarberoInstanciaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service — devuelve las instancias de la semana actual
 * agrupadas por día, ordenadas y con nombres resueltos.
 *
 * Como el endpoint retorna directamente un Map de DTOs (estructura agrupada),
 * el caso de uso se apoya en un adapter de query (Read Model) que devuelve
 * directamente DTOs con los nombres del barbero y tipo de horario resueltos.
 */
@Service
@RequiredArgsConstructor
public class ConsultarHorarioSemanaActualApplicationService implements ConsultarHorarioSemanaActualUseCase {

    private final IHorarioBarberoInstanciaQueryAdapter queryAdapter;

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<DtoHorarioBarberoInstanciaResponse>> consultarSemanaAgrupada() {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return queryAdapter.findResponseByFechaBetween(lunes, domingo)
                .stream()
                .sorted(Comparator.comparing(DtoHorarioBarberoInstanciaResponse::getFecha)
                        .thenComparing(DtoHorarioBarberoInstanciaResponse::getTipoHorario))
                .collect(Collectors.groupingBy(
                        DtoHorarioBarberoInstanciaResponse::getDia,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }
}
