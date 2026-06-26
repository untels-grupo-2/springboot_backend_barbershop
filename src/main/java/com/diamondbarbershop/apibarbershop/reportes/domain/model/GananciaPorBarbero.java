package com.diamondbarbershop.apibarbershop.reportes.domain.model;

public record GananciaPorBarbero(
        Long barberoId,
        String barberoNombre,
        Long totalReservas,
        Long ingresoTotal
) {}
