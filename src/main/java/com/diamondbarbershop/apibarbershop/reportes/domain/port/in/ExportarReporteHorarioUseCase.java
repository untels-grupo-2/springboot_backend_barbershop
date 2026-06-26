package com.diamondbarbershop.apibarbershop.reportes.domain.port.in;

import java.time.LocalDate;

/**
 * Puerto de entrada — exportar el reporte de horarios de barberos como PDF.
 *
 * El admin lo usa desde el panel para descargar el horario semanal y archivarlo
 * o compartirlo. Devuelve el PDF como byte[] que el controller envía como
 * Content-Type: application/pdf.
 */
public interface ExportarReporteHorarioUseCase {

    byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin);
}
