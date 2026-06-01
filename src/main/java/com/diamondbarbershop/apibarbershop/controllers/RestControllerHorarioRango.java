package com.diamondbarbershop.apibarbershop.controllers;

import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.dtos.horarioRangos.response.DtoHorarioRangoResponse;
import com.diamondbarbershop.apibarbershop.services.HorarioRangoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rangos-horario")
@RequiredArgsConstructor
public class RestControllerHorarioRango {

    private final HorarioRangoService horarioRangoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoHorarioRangoResponse>>> listarRangos() {
        List<DtoHorarioRangoResponse> dtoHorarioRangoResponses = horarioRangoService.readAll();
        return ResponseEntity.ok(ApiResponse.succes("Lista de rangos obtenida correctamente",dtoHorarioRangoResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoHorarioRangoResponse>> obtenerRangoPorId(@PathVariable Long id) {
        DtoHorarioRangoResponse dtoRango = horarioRangoService.readOne(id);
        return ResponseEntity.ok(ApiResponse.succes("Rango encontrado",dtoRango));
    }
}
