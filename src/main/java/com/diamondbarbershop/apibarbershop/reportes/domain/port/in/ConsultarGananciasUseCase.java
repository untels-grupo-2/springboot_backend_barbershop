package com.diamondbarbershop.apibarbershop.reportes.domain.port.in;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.ReporteGanancias;

import java.time.LocalDate;

public interface ConsultarGananciasUseCase {

    ReporteGanancias consultar(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre);
}
