package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
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
class ConsultarBarberosApplicationServiceTest {

    @Mock
    private BarberoRepository barberoRepository;

    @InjectMocks
    private ConsultarBarberosApplicationService service;

    @Test
    @DisplayName("Debe retornar la lista de barberos activos del repositorio")
    void should_returnList_when_listarActivos() {
        Barbero b1 = Barbero.crear("Carlos", "http://img.com/carlos.png");
        Barbero b2 = Barbero.crear("Pedro", "http://img.com/pedro.png");
        when(barberoRepository.findActivos()).thenReturn(List.of(b1, b2));

        List<Barbero> resultado = service.listarActivos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(b1, b2);
    }

    @Test
    @DisplayName("Debe retornar el barbero cuando se encuentra por id")
    void should_returnBarbero_when_obtenerFound() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");
        barbero.setId(1L);
        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));

        Barbero resultado = service.obtener(1L);

        assertThat(resultado).isEqualTo(barbero);
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el barbero no se encuentra")
    void should_throwException_when_obtenerNotFound() {
        when(barberoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(BarberoNoEncontradoException.class);
    }
}
