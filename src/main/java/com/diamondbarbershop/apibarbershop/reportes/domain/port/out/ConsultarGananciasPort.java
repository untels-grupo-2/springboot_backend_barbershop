package com.diamondbarbershop.apibarbershop.reportes.domain.port.out;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorBarbero;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorDia;
import com.diamondbarbershop.apibarbershop.reportes.domain.model.GananciaPorServicio;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarGananciasPort {

    List<GananciaPorDia> obtenerGananciasPorDia(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre);

    List<GananciaPorServicio> obtenerGananciasPorServicio(LocalDate fechaInicio, LocalDate fechaFin);

    List<GananciaPorBarbero> obtenerGananciasPorBarbero(LocalDate fechaInicio, LocalDate fechaFin, String servicioNombre);
}
