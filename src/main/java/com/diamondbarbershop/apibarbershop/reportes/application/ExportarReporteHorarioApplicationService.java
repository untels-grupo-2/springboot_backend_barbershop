package com.diamondbarbershop.apibarbershop.reportes.application;

import com.diamondbarbershop.apibarbershop.reportes.domain.port.in.ExportarReporteHorarioUseCase;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.GeneradorReportePdf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Application service — delega al puerto de generación de PDF.
 *
 * No tiene lógica propia — es un thin wrapper para mantener la abstracción.
 * Si en el futuro se agrega lógica (cache, validación de rango, etc.) vive aquí.
 */
@Service
@RequiredArgsConstructor
public class ExportarReporteHorarioApplicationService implements ExportarReporteHorarioUseCase {

    private final GeneradorReportePdf generadorReportePdf;

    @Override
    public byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin) {
        return generadorReportePdf.generarReporteHorario(fechaInicio, fechaFin);
    }
}
