package com.diamondbarbershop.apibarbershop.reportes.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.reportes.domain.model.ReporteGanancias;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.in.ConsultarGananciasUseCase;
import com.diamondbarbershop.apibarbershop.reportes.domain.port.in.ExportarReporteHorarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RestControllerReportes {

    private final ExportarReporteHorarioUseCase exportarReporteHorarioUseCase;
    private final ConsultarGananciasUseCase consultarGananciasUseCase;

    @GetMapping("/reporte/horarios")
    public ResponseEntity<byte[]> exportarHorarioPdf(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        byte[] pdf = exportarReporteHorarioUseCase.exportar(fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=horario_barbero.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reservas/reportes/series")
    public ResponseEntity<ReporteGanancias> consultarGanancias(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value = "servicio", required = false) String servicio) {

        ReporteGanancias reporte = consultarGananciasUseCase.consultar(fechaInicio, fechaFin, servicio);
        return ResponseEntity.ok(reporte);
    }
}
