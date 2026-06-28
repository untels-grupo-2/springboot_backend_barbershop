package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioBarberoBaseRepository;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioInstanciaRepository;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoInstancia;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmarHorarioSemanaSiguienteApplicationServiceTest {

    @Mock
    private HorarioBarberoBaseRepository horarioBaseRepository;

    @Mock
    private HorarioInstanciaRepository horarioInstanciaRepository;

    @InjectMocks
    private ConfirmarHorarioSemanaSiguienteApplicationService service;

    private HorarioBarberoBase crearBase(Long barberoId, Long tipoHorarioId, DiaSemana dia) {
        HorarioBarberoBase base = new HorarioBarberoBase();
        base.setId(1L);
        base.setBarberoId(barberoId);
        base.setTipoHorarioId(tipoHorarioId);
        base.setDia(dia);
        base.setEstId(1);
        base.setEstado(1);
        return base;
    }

    @Test
    @DisplayName("Debe eliminar instancias existentes antes de crear las nuevas")
    void should_deleteExistingInstancias_before_creating() {
        when(horarioBaseRepository.findActivos()).thenReturn(List.of());

        service.confirmar();

        LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate proximoDomingo = proximoLunes.plusDays(6);
        verify(horarioInstanciaRepository).deleteByFechaBetween(proximoLunes, proximoDomingo);
    }

    @Test
    @DisplayName("Debe crear instancia con fecha correcta segun el dia de la semana")
    void should_createInstancia_withCorrectDate() {
        HorarioBarberoBase base = crearBase(10L, 100L, DiaSemana.MIÉRCOLES);
        when(horarioBaseRepository.findActivos()).thenReturn(List.of(base));

        ArgumentCaptor<HorarioBarberoInstancia> captor = ArgumentCaptor.forClass(HorarioBarberoInstancia.class);
        when(horarioInstanciaRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.confirmar();

        HorarioBarberoInstancia instancia = captor.getValue();
        LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate esperada = proximoLunes.plusDays(DiaSemana.MIÉRCOLES.ordinal());

        assertThat(instancia.getBarberoId()).isEqualTo(10L);
        assertThat(instancia.getTipoHorarioId()).isEqualTo(100L);
        assertThat(instancia.getFecha()).isEqualTo(esperada);
        assertThat(instancia.getEstId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe crear una instancia por cada registro base activo")
    void should_createOneInstancia_perActiveBase() {
        List<HorarioBarberoBase> bases = List.of(
                crearBase(10L, 100L, DiaSemana.LUNES),
                crearBase(10L, 200L, DiaSemana.LUNES),
                crearBase(20L, 100L, DiaSemana.MARTES)
        );
        when(horarioBaseRepository.findActivos()).thenReturn(bases);
        when(horarioInstanciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.confirmar();

        verify(horarioInstanciaRepository, times(3)).save(any(HorarioBarberoInstancia.class));
    }

    @Test
    @DisplayName("No debe crear instancias cuando no hay registros base activos")
    void should_notCreateInstancias_when_noActiveBase() {
        when(horarioBaseRepository.findActivos()).thenReturn(List.of());

        service.confirmar();

        verify(horarioInstanciaRepository, never()).save(any());
        verify(horarioInstanciaRepository).deleteByFechaBetween(any(), any());
    }
}
