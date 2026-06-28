package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarServiciosApplicationServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ConsultarServiciosApplicationService service;

    @Test
    @DisplayName("Debe retornar la lista de servicios activos del repositorio")
    void should_returnList_when_listarActivos() {
        Servicio s1 = Servicio.crear("Corte", 30L, "desc1", "http://img.com/1.png");
        Servicio s2 = Servicio.crear("Barba", 20L, "desc2", "http://img.com/2.png");
        when(servicioRepository.findActivos()).thenReturn(List.of(s1, s2));

        List<Servicio> resultado = service.listarActivos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(s1, s2);
    }

    @Test
    @DisplayName("Debe retornar el servicio cuando se encuentra por id")
    void should_returnServicio_when_obtenerFound() {
        Servicio servicio = Servicio.crear("Corte", 30L, "desc", "http://img.com/1.png");
        servicio.setId(1L);
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));

        Servicio resultado = service.obtener(1L);

        assertThat(resultado).isEqualTo(servicio);
        assertThat(resultado.getNombre()).isEqualTo("Corte");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el servicio no se encuentra")
    void should_throwException_when_obtenerNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(ServicioNoEncontradoException.class);
    }
}
