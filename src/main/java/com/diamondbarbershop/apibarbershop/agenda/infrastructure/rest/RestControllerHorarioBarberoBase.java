package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioBaseView;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ActualizarTurnosDiaUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConfirmarHorarioSemanaSiguienteUseCase;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ConsultarHorarioBaseUseCase;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioBase;
import com.diamondbarbershop.apibarbershop.util.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/horarios-base")
@RequiredArgsConstructor
public class RestControllerHorarioBarberoBase {

    private final ActualizarTurnosDiaUseCase actualizarTurnosDiaUseCase;
    private final ConfirmarHorarioSemanaSiguienteUseCase confirmarHorarioUseCase;
    private final ConsultarHorarioBaseUseCase consultarHorarioBaseUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<DiaSemana, List<HorarioBaseView>>>> listarHorarioBase() {
        Map<DiaSemana, List<HorarioBaseView>> horarios = consultarHorarioBaseUseCase.listarAgrupadoPorDia();
        return ResponseEntity.ok(ApiResponse.succes("Horario base obtenido correctamente", horarios));
    }

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
