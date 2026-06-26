package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ActualizarTurnosDiaUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConfirmarHorarioSemanaSiguienteUseCase;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBase;
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

    private final ActualizarTurnosDiaUseCase actualizarTurnosDiaUseCase;
    private final ConfirmarHorarioSemanaSiguienteUseCase confirmarHorarioUseCase;

    @PutMapping
    public ResponseEntity<ApiResponse<Object>> actualizarTurnosDia(@RequestBody DtoHorarioBase dto) {
        actualizarTurnosDiaUseCase.actualizar(new ActualizarTurnosDiaUseCase.ActualizarTurnosDiaCommand(
                dto.getDia(),
                dto.getTurnosPorTipo()
        ));
        return ResponseEntity.ok(ApiResponse.succes("Turnos actualizados correctamente", null));
    }

    @PutMapping("/confirmacion")
    public ResponseEntity<ApiResponse<Object>> confirmarHorario() {
        confirmarHorarioUseCase.confirmar();
        return ResponseEntity.ok(ApiResponse.succes("Horario confirmado para la próxima semana", null));
    }
}
