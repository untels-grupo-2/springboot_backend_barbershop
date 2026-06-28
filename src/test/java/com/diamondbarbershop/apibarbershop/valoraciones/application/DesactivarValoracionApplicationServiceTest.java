package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesactivarValoracionApplicationServiceTest {

    @Mock
    private ValoracionRepository valoracionRepository;

    @InjectMocks
    private DesactivarValoracionApplicationService service;

    @Test
    @DisplayName("Debe desactivar una valoracion existente exitosamente")
    void should_desactivar_when_valoracionExists() {
        Valoracion valoracion = Valoracion.crear(5, true, "Genial", 1L);
        valoracion.setId(1L);
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));
        when(valoracionRepository.save(valoracion)).thenReturn(valoracion);

        service.desactivar(1L);

        assertThat(valoracion.isActiva()).isFalse();
        verify(valoracionRepository).save(valoracion);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al desactivar valoracion inexistente")
    void should_throwException_when_valoracionNotFound() {
        when(valoracionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desactivar(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }
}
