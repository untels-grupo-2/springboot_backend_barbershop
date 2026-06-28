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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeshabilitarServicioApplicationServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private DeshabilitarServicioApplicationService service;

    @Test
    @DisplayName("Debe deshabilitar el servicio exitosamente")
    void should_disableServicio_when_deshabilitarExitoso() {
        Servicio servicio = Servicio.crear("Corte", 30L, "desc", "http://img.com/1.png");
        servicio.setId(1L);
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(servicioRepository.save(servicio)).thenReturn(servicio);

        service.deshabilitar(1L);

        assertThat(servicio.getEstado()).isEqualTo(0);
        assertThat(servicio.isActivo()).isFalse();
        verify(servicioRepository).save(servicio);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el servicio a deshabilitar no existe")
    void should_throwException_when_servicioNoExiste() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deshabilitar(99L))
                .isInstanceOf(ServicioNoEncontradoException.class);
    }
}
