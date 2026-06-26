package com.diamondbarbershop.apibarbershop.reportes.domain.model;

public record GananciaPorServicio(
        Long servicioId,
        String servicioNombre,
        Long totalReservas,
        Long ingresoTotal
) {}
