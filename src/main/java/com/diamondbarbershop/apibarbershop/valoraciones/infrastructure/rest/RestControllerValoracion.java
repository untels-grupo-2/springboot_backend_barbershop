package com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.rest.dto.DtoValoracion;
import com.diamondbarbershop.apibarbershop.valoraciones.infrastructure.rest.dto.DtoValoracionResponse;
import com.diamondbarbershop.apibarbershop.identidad.domain.exception.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.UsuarioJpaEntity;
import com.diamondbarbershop.apibarbershop.identidad.infrastructure.persistance.IUsuarioJpaRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.CrearValoracionUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.DesactivarValoracionUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.in.ListarValoracionesUseCase;
import com.diamondbarbershop.apibarbershop.valoraciones.domain.port.out.ValoracionListadoView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller del BC Valoraciones migrado a hexagonal.
 */
@RestController
@RequestMapping("/valoraciones")
@RequiredArgsConstructor
public class RestControllerValoracion {

    private final CrearValoracionUseCase crearValoracionUseCase;
    private final ListarValoracionesUseCase listarValoracionesUseCase;
    private final DesactivarValoracionUseCase desactivarValoracionUseCase;
    private final IUsuarioJpaRepository usuariosRepository;   // solo para resolver username → id en POST

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> crearValoracion(
            @RequestBody @Valid DtoValoracion dto,
            Authentication authentication) {

        UsuarioJpaEntity usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        crearValoracionUseCase.crear(new CrearValoracionUseCase.CrearValoracionCommand(
                usuario.getUsuario_id(),
                dto.getValoracion(),
                dto.getUtil(),
                dto.getMensaje()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Valoración registrada correctamente", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoValoracionResponse>>> listarValoracion() {
        List<DtoValoracionResponse> dtos = listarValoracionesUseCase.listar()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.succes("Lista de valoraciones obtenida correctamente", dtos));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Object>> cambiarEstado(@PathVariable Long id) {
        desactivarValoracionUseCase.desactivar(id);
        return ResponseEntity.ok(ApiResponse.succes("Valoración respondida", null));
    }

    private DtoValoracionResponse toDto(ValoracionListadoView view) {
        DtoValoracionResponse dto = new DtoValoracionResponse();
        dto.setValoracion_id(view.valoracionId());
        dto.setUsuarioId(view.clienteId());
        dto.setUsuario_nombre(view.clienteNombre());
        dto.setCelular(view.clienteCelular());
        dto.setValoracion(view.puntuacion());
        dto.setUtil(view.util());
        dto.setMensaje(view.mensaje());
        dto.setEstado(view.estado());
        return dto;
    }
}
