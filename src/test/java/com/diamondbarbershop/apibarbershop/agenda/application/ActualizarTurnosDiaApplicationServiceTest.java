package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBarberoBase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ActualizarTurnosDiaUseCase.ActualizarTurnosDiaCommand;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarTurnosDiaApplicationServiceTest {

    @Mock
    private HorarioBarberoBaseRepository horarioBaseRepository;

    @InjectMocks
    private ActualizarTurnosDiaApplicationService service;

    private HorarioBarberoBase crearRegistro(Long id, Long barberoId, Long tipoHorarioId, Integer estId) {
        HorarioBarberoBase registro = new HorarioBarberoBase();
        registro.setId(id);
        registro.setBarberoId(barberoId);
        registro.setTipoHorarioId(tipoHorarioId);
        registro.setDia(DiaSemana.LUNES);
        registro.setEstId(estId);
        registro.setEstado(1);
        return registro;
    }

    @Test
    @DisplayName("Debe asignar turno cuando el barbero esta en el mapa de turnos por tipo")
    void should_asignarTurno_when_barberoInMap() {
        HorarioBarberoBase registro = crearRegistro(1L, 10L, 100L, null);
        when(horarioBaseRepository.findByDia(DiaSemana.LUNES)).thenReturn(List.of(registro));

        Map<Long, List<Long>> turnosPorTipo = Map.of(100L, List.of(10L));
        ActualizarTurnosDiaCommand command = new ActualizarTurnosDiaCommand(DiaSemana.LUNES, turnosPorTipo);

        service.actualizar(command);

        assertThat(registro.getEstId()).isEqualTo(1);
        assertThat(registro.estaActivoEnEsteTurno()).isTrue();
        verify(horarioBaseRepository).saveAll(List.of(registro));
    }

    @Test
    @DisplayName("Debe quitar turno cuando el barbero NO esta en el mapa de turnos por tipo")
    void should_quitarTurno_when_barberoNotInMap() {
        HorarioBarberoBase registro = crearRegistro(1L, 10L, 100L, 1);
        when(horarioBaseRepository.findByDia(DiaSemana.MARTES)).thenReturn(List.of(registro));

        Map<Long, List<Long>> turnosPorTipo = Map.of(100L, List.of(20L));
        ActualizarTurnosDiaCommand command = new ActualizarTurnosDiaCommand(DiaSemana.MARTES, turnosPorTipo);

        service.actualizar(command);

        assertThat(registro.getEstId()).isNull();
        assertThat(registro.estaActivoEnEsteTurno()).isFalse();
        verify(horarioBaseRepository).saveAll(List.of(registro));
    }

    @Test
    @DisplayName("Debe quitar turno cuando el tipoHorarioId no existe en el mapa")
    void should_quitarTurno_when_tipoHorarioNotInMap() {
        HorarioBarberoBase registro = crearRegistro(1L, 10L, 100L, 1);
        when(horarioBaseRepository.findByDia(DiaSemana.VIERNES)).thenReturn(List.of(registro));

        Map<Long, List<Long>> turnosPorTipo = Map.of(200L, List.of(10L));
        ActualizarTurnosDiaCommand command = new ActualizarTurnosDiaCommand(DiaSemana.VIERNES, turnosPorTipo);

        service.actualizar(command);

        assertThat(registro.getEstId()).isNull();
        verify(horarioBaseRepository).saveAll(List.of(registro));
    }

    @Test
    @DisplayName("Debe procesar multiples registros asignando y quitando turnos correctamente")
    void should_processMultipleRegistros_when_mixedAssignments() {
        HorarioBarberoBase registroAsignar = crearRegistro(1L, 10L, 100L, null);
        HorarioBarberoBase registroQuitar = crearRegistro(2L, 20L, 100L, 1);
        HorarioBarberoBase registroMantener = crearRegistro(3L, 30L, 200L, 1);
        List<HorarioBarberoBase> registros = List.of(registroAsignar, registroQuitar, registroMantener);
        when(horarioBaseRepository.findByDia(DiaSemana.LUNES)).thenReturn(registros);

        Map<Long, List<Long>> turnosPorTipo = Map.of(
                100L, List.of(10L),
                200L, List.of(30L)
        );
        ActualizarTurnosDiaCommand command = new ActualizarTurnosDiaCommand(DiaSemana.LUNES, turnosPorTipo);

        service.actualizar(command);

        assertThat(registroAsignar.getEstId()).isEqualTo(1);
        assertThat(registroQuitar.getEstId()).isNull();
        assertThat(registroMantener.getEstId()).isEqualTo(1);
        verify(horarioBaseRepository).saveAll(registros);
    }

    @Test
    @DisplayName("Debe llamar saveAll con lista vacia cuando no hay registros para el dia")
    void should_saveEmptyList_when_noRegistrosForDay() {
        when(horarioBaseRepository.findByDia(DiaSemana.DOMINGO)).thenReturn(List.of());

        Map<Long, List<Long>> turnosPorTipo = Map.of(100L, List.of(10L));
        ActualizarTurnosDiaCommand command = new ActualizarTurnosDiaCommand(DiaSemana.DOMINGO, turnosPorTipo);

        service.actualizar(command);

        verify(horarioBaseRepository).saveAll(List.of());
    }
}
