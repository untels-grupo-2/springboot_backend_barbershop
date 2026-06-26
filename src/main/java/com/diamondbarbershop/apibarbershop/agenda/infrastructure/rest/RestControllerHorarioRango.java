package com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.agenda.domain.model.HorarioRango;
import com.diamondbarbershop.apibarbershop.agenda.domain.port.in.ListarHorarioRangosUseCase;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.agenda.infrastructure.rest.dto.DtoHorarioRangoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rangos-horario")
@RequiredArgsConstructor
public class RestControllerHorarioRango {

    private final ListarHorarioRangosUseCase listarHorarioRangosUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoHorarioRangoResponse>>> listarRangos() {
        List<DtoHorarioRangoResponse> dtos = listarHorarioRangosUseCase.listar().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.succes("Lista de rangos obtenida correctamente", dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoHorarioRangoResponse>> obtenerRangoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(
                "Rango encontrado",
                toDto(listarHorarioRangosUseCase.obtener(id))));
    }

    private DtoHorarioRangoResponse toDto(HorarioRango r) {
        DtoHorarioRangoResponse dto = new DtoHorarioRangoResponse();
        dto.setHorarioRango_id(r.getId());
        dto.setRango(r.getRango());
        dto.setTipoHorario(r.getTipoHorarioNombre());
        return dto;
    }
}
