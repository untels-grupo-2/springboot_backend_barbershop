package com.diamondbarbershop.apibarbershop.reportes.application;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorBarbero;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorDia;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorServicio;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.ReporteGanancias;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.in.ConsultarGananciasUseCase;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.ConsultarGananciasPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultarGananciasApplicationService implements ConsultarGananciasUseCase {

    private final ConsultarGananciasPort consultarGananciasPort;

    @Override
    public ReporteGanancias consultar(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre) {
        List<GananciaPorDia> porDia = consultarGananciasPort.obtenerGananciasPorDia(fechaInicio, fechaFin, servicioNombre);
        List<GananciaPorServicio> porServicio = consultarGananciasPort.obtenerGananciasPorServicio(fechaInicio, fechaFin);
        List<GananciaPorBarbero> porBarbero = consultarGananciasPort.obtenerGananciasPorBarbero(fechaInicio, fechaFin, servicioNombre);

        long totalReservas = porDia.stream().mapToLong(GananciaPorDia::totalReservas).sum();
        long ingresoTotal = porDia.stream().mapToLong(GananciaPorDia::ingresoTotal).sum();
        long ticketPromedio = totalReservas > 0 ? ingresoTotal / totalReservas : 0;

        return new ReporteGanancias(
                fechaInicio,
                fechaFin,
                totalReservas,
                ingresoTotal,
                ticketPromedio,
                porDia,
                porServicio,
                porBarbero
        );
    }
}
