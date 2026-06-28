package com.diamondbarbershop.apibarbershop.reservas.application;

import com.diamondbarbershop.apibarbershop.reservas.domain.port.in.FiltroReservaQuery;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaListadoView;
import com.diamondbarbershop.apibarbershop.reservas.domain.port.out.ReservaRepository;
import com.diamondbarbershop.apibarbershop.reservas.infrastructure.rest.dto.DtoReservaResponse;
import com.diamondbarbershop.apibarbershop.util.EstadoReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarReservasApplicationServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ConsultarReservasApplicationService service;

    @Test
    @DisplayName("Debe retornar pagina mapeada con resultados")
    void should_returnMappedPage_when_resultsExist() {
        FiltroReservaQuery filtro = new FiltroReservaQuery(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        ReservaListadoView view = new ReservaListadoView(
                1L, "Carlos", 2L, "Juan", "Corte clasico",
                "09:00-10:00", EstadoReserva.CREADA, 50L, LocalDate.now()
        );
        Page<ReservaListadoView> pageResult = new PageImpl<>(List.of(view));
        when(reservaRepository.buscarParaListado(filtro, pageable)).thenReturn(pageResult);

        Page<DtoReservaResponse> result = service.listar(filtro, pageable);

        assertThat(result.getContent()).hasSize(1);
        DtoReservaResponse dto = result.getContent().get(0);
        assertThat(dto.getReservaId()).isEqualTo(1L);
        assertThat(dto.getBarberoNombre()).isEqualTo("Carlos");
        assertThat(dto.getUsuarioNombre()).isEqualTo("Juan");
        assertThat(dto.getServicioNombre()).isEqualTo("Corte clasico");
        assertThat(dto.getEstado()).isEqualTo("CREADA");
        assertThat(dto.getMontoTotal()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Debe retornar pagina vacia cuando no hay resultados")
    void should_returnEmptyPage_when_noResults() {
        FiltroReservaQuery filtro = new FiltroReservaQuery(1L, null, EstadoReserva.CREADA, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ReservaListadoView> emptyPage = new PageImpl<>(Collections.emptyList());
        when(reservaRepository.buscarParaListado(filtro, pageable)).thenReturn(emptyPage);

        Page<DtoReservaResponse> result = service.listar(filtro, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}
