package com.diamondbarbershop.apibarbershop.reportes.domain.model;

import java.time.LocalDate;

public record GananciaPorDia(
        LocalDate fecha,
        Long totalReservas,
        Long ingresoTotal
) {}
