package com.diamondbarbershop.apibarbershop.catalogo.application;

import com.diamondbarbershop.apibarbershop.catalogo.domain.exception.ServicioNoEncontradoException;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.Servicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.model.TipoServicio;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.in.CrearServicioUseCase.CrearServicioCommand;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.ServicioRepository;
import com.diamondbarbershop.apibarbershop.catalogo.domain.port.out.TipoServicioRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearServicioApplicationServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private TipoServicioRepository tipoServicioRepository;

    @Mock
    private SubidorImagen subidorImagen;

    @InjectMocks
    private CrearServicioApplicationService service;

    @Test
    @DisplayName("Debe crear un servicio exitosamente y retornar su id")
    void should_returnId_when_crearExitoso() {
        MultipartFile imagen = mock(MultipartFile.class);
        CrearServicioCommand command = new CrearServicioCommand("Corte", 30L, "desc", 1L, imagen);

        when(tipoServicioRepository.findById(1L))
                .thenReturn(Optional.of(TipoServicio.reconstitute(1L, "Corte")));
        when(subidorImagen.subir(eq(imagen), eq("servicios")))
                .thenReturn("http://img.com/corte.png");

        ArgumentCaptor<Servicio> captor = ArgumentCaptor.forClass(Servicio.class);
        when(servicioRepository.save(captor.capture())).thenAnswer(invocation -> {
            Servicio s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Long resultado = service.crear(command);

        assertThat(resultado).isEqualTo(1L);
        Servicio guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Corte");
        assertThat(guardado.getPrecio()).isEqualTo(30L);
        assertThat(guardado.getUrlServicio()).isEqualTo("http://img.com/corte.png");
        assertThat(guardado.getTipoServicioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el tipo de servicio no existe")
    void should_throwException_when_tipoServicioNoExiste() {
        MultipartFile imagen = mock(MultipartFile.class);
        CrearServicioCommand command = new CrearServicioCommand("Corte", 30L, "desc", 99L, imagen);

        when(tipoServicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(command))
                .isInstanceOf(ServicioNoEncontradoException.class);
    }
}
