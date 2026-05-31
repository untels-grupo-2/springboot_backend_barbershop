package com.diamondbarbershop.apibarbershop.controllers;

import com.diamondbarbershop.apibarbershop.services.JasperService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class RestControllerJasper {
    private final JasperService jasperService;

    @GetMapping("/reporte/horarios")
    public ResponseEntity<byte[]> exportarHorarioPdf(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
            ) throws Exception {

        byte[] pdf = jasperService.exportarHorario(Date.valueOf(fechaInicio), Date.valueOf(fechaFin));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=horario_barbero.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
