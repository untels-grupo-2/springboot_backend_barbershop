package com.diamondbarbershop.apibarbershop.personal.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.personal.infrastructure.rest.dto.DtoBarbero;
import com.diamondbarbershop.apibarbershop.personal.infrastructure.rest.dto.DtoBarberoResponse;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.shared.domain.exception.ImagenNoSubidaException;
import com.diamondbarbershop.apibarbershop.personal.domain.model.Barbero;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ActualizarBarberoUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.ConsultarBarberosUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.CrearBarberoUseCase;
import com.diamondbarbershop.apibarbershop.personal.domain.port.in.DeshabilitarBarberoUseCase;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller del BC Personal migrado a hexagonal.
 */
@RestController
@RequestMapping("/barberos")
@RequiredArgsConstructor
public class RestControllerBarbero {

    private final CrearBarberoUseCase crearBarberoUseCase;
    private final ActualizarBarberoUseCase actualizarBarberoUseCase;
    private final ConsultarBarberosUseCase consultarBarberosUseCase;
    private final DeshabilitarBarberoUseCase deshabilitarBarberoUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> crearBarbero(
            @RequestPart("dtoBarbero") @Valid DtoBarbero dtoBarbero,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        validarImagenSiExiste(imagen);

        crearBarberoUseCase.crear(new CrearBarberoUseCase.CrearBarberoCommand(
                dtoBarbero.getNombre(),
                imagen,
                dtoBarbero.getTipoPlantilla()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Barbero creado correctamente", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DtoBarberoResponse>>> listarBarbero() {
        List<DtoBarberoResponse> dtos = consultarBarberosUseCase.listarActivos()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.succes("Lista de barberos obtenida correctamente", dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoBarberoResponse>> obtenerBarberoPorId(@PathVariable Long id) {
        DtoBarberoResponse dto = toDto(consultarBarberosUseCase.obtener(id));
        return ResponseEntity.ok(ApiResponse.succes("Barbero encontrado", dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> actualizarBarbero(
            @PathVariable Long id,
            @RequestPart @Valid DtoBarbero dtoBarbero,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        validarImagenSiExiste(imagen);

        actualizarBarberoUseCase.actualizar(new ActualizarBarberoUseCase.ActualizarBarberoCommand(
                id,
                dtoBarbero.getNombre(),
                imagen
        ));

        DtoBarberoResponse dtoResponse = toDto(consultarBarberosUseCase.obtener(id));
        return ResponseEntity.ok(ApiResponse.succes("Barbero actualizado exitosamente", dtoResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarBarbero(@PathVariable Long id) {
        deshabilitarBarberoUseCase.deshabilitar(id);
        return ResponseEntity.ok(ApiResponse.succes("Barbero deshabilitado exitosamente", null));
    }

    // ── Helpers privados ─────────────────────────────────────────────────────────

    private void validarImagenSiExiste(MultipartFile imagen) {
        if (imagen != null &&
                (imagen.getContentType() == null
                        || !imagen.getContentType().startsWith("image/"))) {
            throw new ImagenNoSubidaException(MensajeError.TIPO_ARCHIVO_NO_PERMITIDO);
        }
    }

    private DtoBarberoResponse toDto(Barbero b) {
        DtoBarberoResponse dto = new DtoBarberoResponse();
        dto.setBarbero_id(b.getId());
        dto.setNombre(b.getNombre());
        dto.setUrlBarbero(b.getUrlBarbero());
        return dto;
    }
}
