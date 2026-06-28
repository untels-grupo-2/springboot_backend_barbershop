package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionListadoView;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarValoracionesApplicationServiceTest {

    @Mock
    private ValoracionRepository valoracionRepository;

    @InjectMocks
    private ListarValoracionesApplicationService service;

    @Test
    @DisplayName("Debe retornar la lista de valoraciones del repositorio")
    void should_returnList_when_valoracionesExist() {
        List<ValoracionListadoView> expected = List.of(
                new ValoracionListadoView(1L, 10L, "Juan Perez", "999111222", 5, true, "Excelente", 1),
                new ValoracionListadoView(2L, 20L, "Maria Lopez", "999333444", 3, false, "Regular", 1)
        );
        when(valoracionRepository.buscarParaListado()).thenReturn(expected);

        List<ValoracionListadoView> result = service.listar();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay valoraciones")
    void should_returnEmptyList_when_noValoraciones() {
        when(valoracionRepository.buscarParaListado()).thenReturn(Collections.emptyList());

        List<ValoracionListadoView> result = service.listar();

        assertThat(result).isEmpty();
    }
}
