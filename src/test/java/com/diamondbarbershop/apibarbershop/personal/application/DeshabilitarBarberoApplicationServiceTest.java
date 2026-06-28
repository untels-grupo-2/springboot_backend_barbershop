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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeshabilitarBarberoApplicationServiceTest {

    @Mock
    private BarberoRepository barberoRepository;

    @InjectMocks
    private DeshabilitarBarberoApplicationService service;

    @Test
    @DisplayName("Debe deshabilitar el barbero exitosamente")
    void should_disableBarbero_when_deshabilitarExitoso() {
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/carlos.png");
        barbero.setId(1L);
        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(barberoRepository.save(barbero)).thenReturn(barbero);

        service.deshabilitar(1L);

        assertThat(barbero.getEstado()).isEqualTo(0);
        assertThat(barbero.isActivo()).isFalse();
        verify(barberoRepository).save(barbero);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el barbero a deshabilitar no existe")
    void should_throwException_when_barberoNoExiste() {
        when(barberoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deshabilitar(99L))
                .isInstanceOf(BarberoNoEncontradoException.class);
    }
}
