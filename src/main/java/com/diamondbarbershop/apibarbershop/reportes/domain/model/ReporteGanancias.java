package com.diamondbarbershop.apibarbershop.reportes.domain.model;

import java.time.LocalDate;
import java.util.List;

public record ReporteGanancias(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Long totalReservas,
        Long ingresoTotal,
        Long ticketPromedio,
        List<GananciaPorDia> desglosePorDia,
        List<GananciaPorServicio> desglosePorServicio,
        List<GananciaPorBarbero> desglosePorBarbero
) {}
