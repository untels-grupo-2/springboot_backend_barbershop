package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarHorarioBaseApplicationServiceTest {

    @Mock
    private HorarioBarberoBaseRepository horarioBaseRepository;

    @InjectMocks
    private ConsultarHorarioBaseApplicationService service;

    @Test
    @DisplayName("Debe agrupar horarios por dia de la semana")
    void should_groupByDay() {
        List<HorarioBaseView> views = List.of(
                new HorarioBaseView(1L, 10L, "Carlos", 100L, "Mañana", DiaSemana.LUNES, true),
                new HorarioBaseView(2L, 10L, "Carlos", 200L, "Tarde", DiaSemana.LUNES, false),
                new HorarioBaseView(3L, 20L, "Luis", 100L, "Mañana", DiaSemana.MARTES, true)
        );
        when(horarioBaseRepository.findAllActivosComoView()).thenReturn(views);

        Map<DiaSemana, List<HorarioBaseView>> resultado = service.listarAgrupadoPorDia();

        assertThat(resultado).containsOnlyKeys(DiaSemana.LUNES, DiaSemana.MARTES);
        assertThat(resultado.get(DiaSemana.LUNES)).hasSize(2);
        assertThat(resultado.get(DiaSemana.MARTES)).hasSize(1);
    }

    @Test
    @DisplayName("Debe retornar mapa vacio cuando no hay registros activos")
    void should_returnEmptyMap_when_noActiveRecords() {
        when(horarioBaseRepository.findAllActivosComoView()).thenReturn(List.of());

        Map<DiaSemana, List<HorarioBaseView>> resultado = service.listarAgrupadoPorDia();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe mantener el campo asignado segun est_id")
    void should_preserveAsignado_fromEstId() {
        List<HorarioBaseView> views = List.of(
                new HorarioBaseView(1L, 10L, "Carlos", 100L, "Mañana", DiaSemana.LUNES, true),
                new HorarioBaseView(2L, 10L, "Carlos", 200L, "Tarde", DiaSemana.LUNES, false)
        );
        when(horarioBaseRepository.findAllActivosComoView()).thenReturn(views);

        Map<DiaSemana, List<HorarioBaseView>> resultado = service.listarAgrupadoPorDia();

        List<HorarioBaseView> lunes = resultado.get(DiaSemana.LUNES);
        assertThat(lunes.get(0).asignado()).isTrue();
        assertThat(lunes.get(1).asignado()).isFalse();
    }
}
