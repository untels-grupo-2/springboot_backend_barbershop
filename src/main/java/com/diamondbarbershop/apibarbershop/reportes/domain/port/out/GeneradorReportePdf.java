package com.diamondbarbershop.apibarbershop.reportes.domain.port.out;

import java.time.LocalDate;

/**
 * Puerto de salida — generación de reporte PDF.
 *
 * El dominio del BC Reportes no conoce JasperReports; solo confía en este
 * contrato. El adapter de infraestructura (JasperPdfGeneradorAdapter)
 * traduce a llamadas reales al motor de Jasper.
 *
 * Beneficio: si en el futuro se cambia Jasper por iText o cualquier otro
 * motor PDF, solo cambia el adapter.
 */
public interface GeneradorReportePdf {

    byte[] generarReporteHorario(LocalDate fechaInicio, LocalDate fechaFin);
}
