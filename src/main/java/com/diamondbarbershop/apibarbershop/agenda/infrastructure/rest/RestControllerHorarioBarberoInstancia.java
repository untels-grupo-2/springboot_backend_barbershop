package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConsultarHorarioSemanaActualUseCase;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBarberoInstanciaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/horarios-semana")
@RequiredArgsConstructor
public class RestControllerHorarioBarberoInstancia {

    private final ConsultarHorarioSemanaActualUseCase consultarSemanaActualUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<DtoHorarioBarberoInstanciaResponse>>>> obtenerSemanaAgrupada() {
        return ResponseEntity.ok(ApiResponse.succes(
                "Horario Actual:",
                consultarSemanaActualUseCase.consultarSemanaAgrupada()));
    }
}
