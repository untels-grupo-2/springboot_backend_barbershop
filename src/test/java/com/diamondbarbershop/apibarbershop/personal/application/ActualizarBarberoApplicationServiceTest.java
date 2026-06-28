package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.personal.domain.exception.BarberoNoEncontradoException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ActualizarBarberoUseCase.ActualizarBarberoCommand;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
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
class ActualizarBarberoApplicationServiceTest {

    @Mock
    private BarberoRepository barberoRepository;

    @Mock
    private SubidorImagen subidorImagen;

    @InjectMocks
    private ActualizarBarberoApplicationService service;

    @Test
    @DisplayName("Debe actualizar un barbero exitosamente con imagen nueva")
    void should_updateBarbero_when_actualizarConImagenNueva() {
        MultipartFile imagen = mock(MultipartFile.class);
        ActualizarBarberoCommand command = new ActualizarBarberoCommand(1L, "Pedro", imagen);
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/old.png");
        barbero.setId(1L);

        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(subidorImagen.subir(eq(imagen), eq("barberos"))).thenReturn("http://img.com/new.png");
        when(barberoRepository.save(any(Barbero.class))).thenAnswer(inv -> inv.getArgument(0));

        service.actualizar(command);

        assertThat(barbero.getNombre()).isEqualTo("Pedro");
        assertThat(barbero.getUrlBarbero()).isEqualTo("http://img.com/new.png");
        verify(barberoRepository).save(barbero);
    }

    @Test
    @DisplayName("Debe actualizar un barbero exitosamente sin imagen preservando la URL existente")
    void should_preserveUrl_when_actualizarSinImagen() {
        ActualizarBarberoCommand command = new ActualizarBarberoCommand(1L, "Pedro", null);
        Barbero barbero = Barbero.crear("Carlos", "http://img.com/original.png");
        barbero.setId(1L);

        when(barberoRepository.findById(1L)).thenReturn(Optional.of(barbero));
        when(barberoRepository.save(any(Barbero.class))).thenAnswer(inv -> inv.getArgument(0));

        service.actualizar(command);

        assertThat(barbero.getNombre()).isEqualTo("Pedro");
        assertThat(barbero.getUrlBarbero()).isEqualTo("http://img.com/original.png");
        verify(barberoRepository).save(barbero);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el barbero a actualizar no existe")
    void should_throwException_when_barberoNoExiste() {
        ActualizarBarberoCommand command = new ActualizarBarberoCommand(99L, "Pedro", null);

        when(barberoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(command))
                .isInstanceOf(BarberoNoEncontradoException.class);
    }
}
