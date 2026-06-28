package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConsultarHorarioBaseUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultarHorarioBaseApplicationService implements ConsultarHorarioBaseUseCase {

    private final HorarioBarberoBaseRepository horarioBaseRepository;

    @Override
    public Map<DiaSemana, List<HorarioBaseView>> listarAgrupadoPorDia() {
        List<HorarioBaseView> todos = horarioBaseRepository.findAllActivosComoView();

        return todos.stream()
                .collect(Collectors.groupingBy(
                        HorarioBaseView::dia,
                        () -> new LinkedHashMap<>(),
                        Collectors.toList()
                ));
    }
}
