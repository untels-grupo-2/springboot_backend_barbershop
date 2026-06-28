package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarTiposServicioApplicationServiceTest {

    @Mock
    private TipoServicioRepository tipoServicioRepository;

    @InjectMocks
    private ListarTiposServicioApplicationService service;

    @Test
    @DisplayName("Debe retornar todos los tipos de servicio del repositorio")
    void should_returnAllTipos_when_listar() {
        TipoServicio t1 = TipoServicio.reconstitute(1L, "Corte");
        TipoServicio t2 = TipoServicio.reconstitute(2L, "Barba");
        when(tipoServicioRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TipoServicio> resultado = service.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(t1, t2);
    }
}
