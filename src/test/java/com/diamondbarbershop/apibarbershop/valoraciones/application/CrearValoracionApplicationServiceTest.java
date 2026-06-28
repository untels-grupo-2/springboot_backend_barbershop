package com.diamondbarbershop.apibarbershop.valoraciones.application;

import com.diamondbarbershop.apibarbershop.valoraciones.domain.model.Valoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.CrearValoracionUseCase.CrearValoracionCommand;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearValoracionApplicationServiceTest {

    @Mock
    private ValoracionRepository valoracionRepository;

    @InjectMocks
    private CrearValoracionApplicationService service;

    @Captor
    private ArgumentCaptor<Valoracion> valoracionCaptor;

    @Test
    @DisplayName("Debe crear una valoracion exitosamente y retornar su id")
    void should_createAndReturnId_when_commandIsValid() {
        CrearValoracionCommand command = new CrearValoracionCommand(10L, 4, true, "Muy buen servicio");

        when(valoracionRepository.save(any(Valoracion.class))).thenAnswer(invocation -> {
            Valoracion v = invocation.getArgument(0);
            v.setId(99L);
            return v;
        });

        Long id = service.crear(command);

        assertThat(id).isEqualTo(99L);

        verify(valoracionRepository).save(valoracionCaptor.capture());
        Valoracion captured = valoracionCaptor.getValue();
        assertThat(captured.getPuntuacion()).isEqualTo(4);
        assertThat(captured.getUtil()).isTrue();
        assertThat(captured.getMensaje()).isEqualTo("Muy buen servicio");
        assertThat(captured.getClienteId()).isEqualTo(10L);
        assertThat(captured.getEstado()).isEqualTo(1);
    }
}
