package com.diamondbarbershop.apibarbershop.reportes.infrastructure.jasper;

import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.GeneradorReportePdf;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador — implementa GeneradorReportePdf usando JasperReports.
 *
 * Lee la plantilla .jrxml de resources/reports/ y genera un PDF a partir de
 * los datos del rango de fechas indicado, conectándose a la BD directamente
 * para que Jasper ejecute sus queries.
 */
@Component
@RequiredArgsConstructor
public class JasperPdfGeneradorAdapter implements GeneradorReportePdf {

    private static final String PLANTILLA_PATH = "reports/HorarioBarberoReporte.jrxml";

    private final DataSource dataSource;

    @Override
    public byte[] generarReporteHorario(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            InputStream reportStream = new ClassPathResource(PLANTILLA_PATH).getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> params = new HashMap<>();
            params.put("FechaInicio", Date.valueOf(fechaInicio));
            params.put("FechaFin", Date.valueOf(fechaFin));

            Connection conn = DataSourceUtils.getConnection(dataSource);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }
}
