package com.diamondbarbershop.apibarbershop.reportes.application;

import com.diamondbarbershop.apibarbershop.reportes.domain.port.out.GeneradorReportePdf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportarReporteHorarioApplicationServiceTest {

    @Mock
    private GeneradorReportePdf generadorReportePdf;

    @InjectMocks
    private ExportarReporteHorarioApplicationService service;

    @Test
    @DisplayName("Debe delegar al generador de PDF y retornar el byte array")
    void should_returnByteArray_when_exportarIsCalled() {
        LocalDate fechaInicio = LocalDate.of(2025, 3, 1);
        LocalDate fechaFin = LocalDate.of(2025, 3, 31);
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46};

        when(generadorReportePdf.generarReporteHorario(fechaInicio, fechaFin)).thenReturn(pdfBytes);

        byte[] result = service.exportar(fechaInicio, fechaFin);

        assertThat(result).isEqualTo(pdfBytes);
        verify(generadorReportePdf).generarReporteHorario(fechaInicio, fechaFin);
    }
}
