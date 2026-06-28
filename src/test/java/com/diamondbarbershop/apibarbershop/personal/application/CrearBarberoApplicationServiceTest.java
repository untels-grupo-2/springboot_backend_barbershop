package com.diamondbarbershop.apibarbershop.personal.application;

import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreator;
import com.diamondbarbershop.apibarbershop.agenda.application.factory.HorarioBaseTemplateCreatorSelector;
import com.diamondbarbershop.apibarbershop.agenda.domain.model.TipoPlantillaHorario;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.CrearBarberoUseCase.CrearBarberoCommand;
import com.diamondbarbershop.apibarbershop.personal.domain.port.out.BarberoRepository;
import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearBarberoApplicationServiceTest {

    @Mock
    private BarberoRepository barberoRepository;

    @Mock
    private SubidorImagen subidorImagen;

    @Mock
    private HorarioBaseTemplateCreatorSelector horarioBaseTemplateCreatorSelector;

    @InjectMocks
    private CrearBarberoApplicationService service;

    @Test
    @DisplayName("Debe crear un barbero exitosamente con imagen y plantilla de horario")
    void should_returnId_when_crearExitosoConImagen() {
        MultipartFile imagen = mock(MultipartFile.class);
        CrearBarberoCommand command = new CrearBarberoCommand("Carlos", imagen, TipoPlantillaHorario.COMPLETA);
        HorarioBaseTemplateCreator creator = mock(HorarioBaseTemplateCreator.class);

        when(subidorImagen.subir(eq(imagen), eq("barberos"))).thenReturn("http://img.com/carlos.png");

        ArgumentCaptor<Barbero> captor = ArgumentCaptor.forClass(Barbero.class);
        when(barberoRepository.save(captor.capture())).thenAnswer(invocation -> {
            Barbero b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });
        when(horarioBaseTemplateCreatorSelector.seleccionar(TipoPlantillaHorario.COMPLETA)).thenReturn(creator);

        Long resultado = service.crear(command);

        assertThat(resultado).isEqualTo(1L);
        Barbero guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Carlos");
        assertThat(guardado.getUrlBarbero()).isEqualTo("http://img.com/carlos.png");
        assertThat(guardado.getEstado()).isEqualTo(1);
        verify(creator).crear(1L);
    }

    @Test
    @DisplayName("Debe crear un barbero exitosamente sin imagen")
    void should_returnId_when_crearExitosoSinImagen() {
        CrearBarberoCommand command = new CrearBarberoCommand("Carlos", null, TipoPlantillaHorario.FIN_DE_SEMANA);
        HorarioBaseTemplateCreator creator = mock(HorarioBaseTemplateCreator.class);

        when(barberoRepository.save(org.mockito.ArgumentMatchers.any(Barbero.class))).thenAnswer(invocation -> {
            Barbero b = invocation.getArgument(0);
            b.setId(2L);
            return b;
        });
        when(horarioBaseTemplateCreatorSelector.seleccionar(TipoPlantillaHorario.FIN_DE_SEMANA)).thenReturn(creator);

        Long resultado = service.crear(command);

        assertThat(resultado).isEqualTo(2L);
        verify(creator).crear(2L);
    }
}
