package com.diamondbarbershop.apibarbershop.controllers;

import com.diamondbarbershop.apibarbershop.dtos.common.ApiResponse;
import com.diamondbarbershop.apibarbershop.dtos.horarioBase.DtoHorarioBase;
import com.diamondbarbershop.apibarbershop.services.HorarioBarberoBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horarios-base")
@RequiredArgsConstructor
public class RestControllerHorarioBarberoBase {
    private final HorarioBarberoBaseService horarioBarberoBaseService;


    @PutMapping
    public ResponseEntity<ApiResponse<Object>> actualizarTurnosDia(@RequestBody DtoHorarioBase dtoHorarioBase){
        horarioBarberoBaseService.actualizarTurnosDia(dtoHorarioBase);
        return ResponseEntity.ok(ApiResponse.succes("Turnos actualizados correctamente",null));
    }

    @PutMapping("/confirmacion")
    public ResponseEntity<ApiResponse<Object>> confirmarHorario(){
        horarioBarberoBaseService.confirmarHorarioBaseParaSemanasSiguientes();
        return ResponseEntity.ok(ApiResponse.succes("Horario confirmado para la próxima semana",null));
    }
}
