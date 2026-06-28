package com.diamondbarbershop.apibarbershop.reportes.application;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorBarbero;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorDia;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorServicio;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.ReporteGanancias;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.ConsultarGananciasPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarGananciasApplicationServiceTest {

    @Mock
    private ConsultarGananciasPort consultarGananciasPort;

    @InjectMocks
    private ConsultarGananciasApplicationService service;

    private final LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
    private final LocalDate fechaFin = LocalDate.of(2025, 1, 31);

    @Test
    @DisplayName("Debe calcular totales y ticket promedio correctamente con datos")
    void should_calculateTotals_when_dataExists() {
        List<GananciaPorDia> porDia = List.of(
                new GananciaPorDia(LocalDate.of(2025, 1, 10), 5L, 500L),
                new GananciaPorDia(LocalDate.of(2025, 1, 11), 3L, 300L)
        );
        List<GananciaPorServicio> porServicio = List.of(
                new GananciaPorServicio(1L, "Corte clasico", 8L, 800L)
        );
        List<GananciaPorBarbero> porBarbero = List.of(
                new GananciaPorBarbero(1L, "Carlos", 8L, 800L)
        );

        when(consultarGananciasPort.obtenerGananciasPorDia(fechaInicio, fechaFin, null)).thenReturn(porDia);
        when(consultarGananciasPort.obtenerGananciasPorServicio(fechaInicio, fechaFin)).thenReturn(porServicio);
        when(consultarGananciasPort.obtenerGananciasPorBarbero(fechaInicio, fechaFin, null)).thenReturn(porBarbero);

        ReporteGanancias reporte = service.consultar(fechaInicio, fechaFin, null);

        assertThat(reporte.totalReservas()).isEqualTo(8L);
        assertThat(reporte.ingresoTotal()).isEqualTo(800L);
        assertThat(reporte.ticketPromedio()).isEqualTo(100L);
        assertThat(reporte.desglosePorDia()).hasSize(2);
        assertThat(reporte.desglosePorServicio()).hasSize(1);
        assertThat(reporte.desglosePorBarbero()).hasSize(1);
        assertThat(reporte.fechaInicio()).isEqualTo(fechaInicio);
        assertThat(reporte.fechaFin()).isEqualTo(fechaFin);
    }

    @Test
    @DisplayName("Debe retornar totales en cero y ticket promedio cero cuando no hay datos")
    void should_returnZeros_when_noData() {
        when(consultarGananciasPort.obtenerGananciasPorDia(fechaInicio, fechaFin, null))
                .thenReturn(Collections.emptyList());
        when(consultarGananciasPort.obtenerGananciasPorServicio(fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());
        when(consultarGananciasPort.obtenerGananciasPorBarbero(fechaInicio, fechaFin, null))
                .thenReturn(Collections.emptyList());

        ReporteGanancias reporte = service.consultar(fechaInicio, fechaFin, null);

        assertThat(reporte.totalReservas()).isEqualTo(0L);
        assertThat(reporte.ingresoTotal()).isEqualTo(0L);
        assertThat(reporte.ticketPromedio()).isEqualTo(0L);
        assertThat(reporte.desglosePorDia()).isEmpty();
        assertThat(reporte.desglosePorServicio()).isEmpty();
        assertThat(reporte.desglosePorBarbero()).isEmpty();
    }

    @Test
    @DisplayName("Debe pasar el filtro de servicio a las consultas por dia y por barbero")
    void should_passServiceFilter_when_servicioNombreProvided() {
        String filtroServicio = "Corte clasico";

        when(consultarGananciasPort.obtenerGananciasPorDia(fechaInicio, fechaFin, filtroServicio))
                .thenReturn(List.of(new GananciaPorDia(LocalDate.of(2025, 1, 10), 2L, 200L)));
        when(consultarGananciasPort.obtenerGananciasPorServicio(fechaInicio, fechaFin))
                .thenReturn(Collections.emptyList());
        when(consultarGananciasPort.obtenerGananciasPorBarbero(fechaInicio, fechaFin, filtroServicio))
                .thenReturn(Collections.emptyList());

        service.consultar(fechaInicio, fechaFin, filtroServicio);

        verify(consultarGananciasPort).obtenerGananciasPorDia(fechaInicio, fechaFin, filtroServicio);
        verify(consultarGananciasPort).obtenerGananciasPorBarbero(fechaInicio, fechaFin, filtroServicio);
        verify(consultarGananciasPort).obtenerGananciasPorServicio(fechaInicio, fechaFin);
    }
}
