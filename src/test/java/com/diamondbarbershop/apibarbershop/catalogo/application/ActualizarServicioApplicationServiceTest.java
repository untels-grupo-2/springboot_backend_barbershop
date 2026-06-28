package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.ActualizarServicioUseCase.ActualizarServicioCommand;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarServicioApplicationServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private TipoServicioRepository tipoServicioRepository;

    @Mock
    private SubidorImagen subidorImagen;

    @InjectMocks
    private ActualizarServicioApplicationService service;

    @Test
    @DisplayName("Debe actualizar un servicio exitosamente con imagen nueva")
    void should_updateServicio_when_actualizarConImagenNueva() {
        MultipartFile imagen = mock(MultipartFile.class);
        ActualizarServicioCommand command = new ActualizarServicioCommand(1L, "Corte premium", 50L, "desc nueva", 2L, imagen);
        Servicio servicio = Servicio.crear("Corte", 30L, "desc", "http://img.com/old.png");
        servicio.setId(1L);
        servicio.setTipoServicioId(1L);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(tipoServicioRepository.findById(2L)).thenReturn(Optional.of(TipoServicio.reconstitute(2L, "Barba")));
        when(subidorImagen.subir(eq(imagen), eq("servicios"))).thenReturn("http://img.com/new.png");
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        service.actualizar(command);

        assertThat(servicio.getNombre()).isEqualTo("Corte premium");
        assertThat(servicio.getUrlServicio()).isEqualTo("http://img.com/new.png");
        verify(servicioRepository).save(servicio);
    }

    @Test
    @DisplayName("Debe actualizar un servicio exitosamente sin imagen preservando la URL existente")
    void should_preserveUrl_when_actualizarSinImagen() {
        ActualizarServicioCommand command = new ActualizarServicioCommand(1L, "Corte premium", 50L, "desc nueva", 2L, null);
        Servicio servicio = Servicio.crear("Corte", 30L, "desc", "http://img.com/original.png");
        servicio.setId(1L);
        servicio.setTipoServicioId(1L);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(tipoServicioRepository.findById(2L)).thenReturn(Optional.of(TipoServicio.reconstitute(2L, "Barba")));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        service.actualizar(command);

        assertThat(servicio.getUrlServicio()).isEqualTo("http://img.com/original.png");
        verify(servicioRepository).save(servicio);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el servicio a actualizar no existe")
    void should_throwException_when_servicioNoExiste() {
        ActualizarServicioCommand command = new ActualizarServicioCommand(99L, "Corte", 30L, "desc", 1L, null);

        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(command))
                .isInstanceOf(ServicioNoEncontradoException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el tipo de servicio no existe al actualizar")
    void should_throwException_when_tipoServicioNoExisteAlActualizar() {
        ActualizarServicioCommand command = new ActualizarServicioCommand(1L, "Corte", 30L, "desc", 99L, null);
        Servicio servicio = Servicio.crear("Corte", 30L, "desc", "http://img.com/x.png");
        servicio.setId(1L);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(tipoServicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(command))
                .isInstanceOf(ServicioNoEncontradoException.class);
    }
}
