package com.diamondbarbershop.apibarbershop.agenda.application;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.out.HorarioRangoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarHorarioRangosApplicationServiceTest {

    @Mock
    private HorarioRangoRepository horarioRangoRepository;

    @InjectMocks
    private ListarHorarioRangosApplicationService service;

    @Test
    @DisplayName("Debe retornar todos los rangos horarios al listar")
    void should_returnAllRangos_when_listar() {
        HorarioRango rango1 = HorarioRango.reconstitute(1L, "08:00-08:30", 1L, "Manana");
        HorarioRango rango2 = HorarioRango.reconstitute(2L, "08:30-09:00", 1L, "Manana");
        HorarioRango rango3 = HorarioRango.reconstitute(3L, "14:00-14:30", 2L, "Tarde");
        when(horarioRangoRepository.findAll()).thenReturn(List.of(rango1, rango2, rango3));

        List<HorarioRango> resultado = service.listar();

        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactly(rango1, rango2, rango3);
        verify(horarioRangoRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay rangos")
    void should_returnEmptyList_when_noRangosExist() {
        when(horarioRangoRepository.findAll()).thenReturn(List.of());

        List<HorarioRango> resultado = service.listar();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar el rango horario cuando existe el id")
    void should_returnRango_when_idExists() {
        HorarioRango rango = HorarioRango.reconstitute(1L, "08:00-08:30", 1L, "Manana");
        when(horarioRangoRepository.findById(1L)).thenReturn(Optional.of(rango));

        HorarioRango resultado = service.obtener(1L);

        assertThat(resultado).isEqualTo(rango);
        assertThat(resultado.getRango()).isEqualTo("08:00-08:30");
        assertThat(resultado.getTipoHorarioNombre()).isEqualTo("Manana");
        verify(horarioRangoRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException cuando el rango horario no existe")
    void should_throwRuntimeException_when_idDoesNotExist() {
        when(horarioRangoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }
}
